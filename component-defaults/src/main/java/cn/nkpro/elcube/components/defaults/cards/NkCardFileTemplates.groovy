/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.components.defaults.cards

import cn.nkpro.elcube.annotation.NkNote
import cn.nkpro.elcube.basic.NkProperties
import cn.nkpro.elcube.co.NkCustomObjectManager
import cn.nkpro.elcube.docengine.NkAbstractCard
import cn.nkpro.elcube.docengine.interceptor.NkFileTemplateMetadataPreprocessor
import cn.nkpro.elcube.docengine.model.DocDefIV
import cn.nkpro.elcube.docengine.model.DocHV
import cn.nkpro.elcube.exception.NkDefineException
import cn.nkpro.elcube.exception.NkSystemException
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions
import fr.opensagres.xdocreport.core.XDocReportException
import fr.opensagres.xdocreport.document.IXDocReport
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry
import fr.opensagres.xdocreport.template.IContext
import fr.opensagres.xdocreport.template.TemplateEngineKind
import org.apache.commons.lang3.StringUtils
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.text.Format
import java.text.SimpleDateFormat

@NkNote("文件模版")
@Component("NkCardFileTemplates")
class NkCardFileTemplates extends NkAbstractCard<Map, NkCardFileTemplateDefBO> {
    private Format format = new SimpleDateFormat(String.format("yyyyMMdd%sHHmmss", File.separator))
    @Autowired
    private NkProperties properties
    @Autowired
    private NkCustomObjectManager nkCustomObjectManager


    @Override
    Object callDef(NkCardFileTemplateDefBO nkCardFileTemplateDefBO, Object options) {
        //获取导出处理程序的方法
        if (StringUtils.equals("findHandlerClassOp", options as String)) {
            return nkCustomObjectManager.getCustomObjectDescriptionList(NkFileTemplateMetadataPreprocessor.class, true, null)
        }
        //下载模板文件
        List<NkCardFileTemplateDefTBO> items = nkCardFileTemplateDefBO.getItems()
        NkCardFileTemplateDefTBO nkCardFileTemplateDefTBO = items.stream().filter { i -> StringUtils.equals(i.getTemplateCode(), options as String) }
                .findFirst().orElse(null)
        if (nkCardFileTemplateDefTBO == null || StringUtils.isBlank(nkCardFileTemplateDefTBO.getTemplateBase64())) {
            return ""
        }
        String base64str = nkCardFileTemplateDefTBO.getTemplateBase64().substring(nkCardFileTemplateDefTBO.getTemplateBase64().indexOf("base64,") + 7)
        byte[] bytes = Base64.getDecoder().decode(base64str)
        if (bytes == null) {
            return ""
        }
        String url = String.join(File.separator, ".NkCardFileTemplates", format.format(new Date()), UUIDHexGenerator.generate(), nkCardFileTemplateDefTBO.getTemplateFileName()).replace("\\", "/")
        String path = String.join(File.separator, properties.getFileRootPath(), url).replace("\\", "/")
        File targetFile = new File(path)
        if (targetFile.getParentFile().mkdirs()) {
            FileOutputStream fos = new FileOutputStream(targetFile)
            fos.write(bytes, 0, bytes.length)
            fos.flush()
            fos.close()
            return url
        }
        return super.callDef(nkCardFileTemplateDefBO, options)
    }

    @Override
    Object call(DocHV doc, Map map, DocDefIV defIV, NkCardFileTemplateDefBO d, Object options) {
        String optionsStr = options as String
        if (optionsStr.startsWith("export:")) {
            NkCardFileTemplateDefTBO nkCardFileTemplateDefTBO = d.items.stream().filter({ i -> StringUtils.equals(i.getTemplateCode(), optionsStr.substring(7)) })
                    .findFirst().orElse(null)
            if (nkCardFileTemplateDefTBO == null || StringUtils.isBlank(nkCardFileTemplateDefTBO.getTemplateBase64())) {
                return ""
            }
            String base64str = nkCardFileTemplateDefTBO.getTemplateBase64().substring(nkCardFileTemplateDefTBO.getTemplateBase64().indexOf("base64,") + 7)
            byte[] bytes = convert(doc, Base64.getDecoder().decode(base64str), nkCardFileTemplateDefTBO)
            if (bytes == null) {
                return ""
            }
            String fileNm = nkCardFileTemplateDefTBO.getTemplateFileName()
            int i = fileNm.lastIndexOf(".")
            String wordFileName = StringUtils.isNotBlank(nkCardFileTemplateDefTBO.getTemplateDesc()) ? nkCardFileTemplateDefTBO.getTemplateDesc()+fileNm.substring(i) : fileNm.substring(0, fileNm.indexOf(".")) + fileNm.substring(i)
            String wordUrl = String.join(File.separator, ".NkCardFileTemplates", format.format(new Date()), UUIDHexGenerator.generate(), wordFileName as String).replace("\\", "/")
            String wordPath = String.join(File.separator, properties.getFileRootPath(), wordUrl).replace("\\", "/")
            File targetFile = new File(wordPath)
            if (targetFile.getParentFile().mkdirs()) {
                FileOutputStream fos = new FileOutputStream(targetFile)
                fos.write(bytes, 0, bytes.length)
                fos.flush()
                fos.close()
            }
            if (StringUtils.equals(nkCardFileTemplateDefTBO.getExportType(), "1")) {
                String pdfFileName = nkCardFileTemplateDefTBO.getTemplateDesc() + ".pdf"
                try {
                    XWPFDocument document = new XWPFDocument(new FileInputStream(new File(wordPath)))
                    String pdfUrl = String.join(File.separator, ".NkCardFileTemplates", format.format(new Date()), UUIDHexGenerator.generate(), pdfFileName as String).replace("\\", "/")
                    String pdfPath = String.join(File.separator, properties.getFileRootPath(), pdfUrl).replace("\\", "/")
                    File outFile = new File(pdfPath)
                    if (outFile.getParentFile().mkdirs()) {
                        OutputStream out = new FileOutputStream(outFile)
                        PdfOptions options1 = PdfOptions.create()  //gb2312
                        PdfConverter.getInstance().convert(document, out, options1)
                    }
                    return pdfUrl
                }
                catch (Exception e) {
                    e.printStackTrace()
                }
            } else {
                return wordUrl
            }
        }
        return super.call(doc, map, defIV, d, options)
    }

    private byte[] convert(DocHV doc, byte[] template, NkCardFileTemplateDefTBO nkCardFileTemplateDefTBO) throws NkSystemException {
        NkFileTemplateMetadataPreprocessor preprocessors
        if (StringUtils.isNotBlank(nkCardFileTemplateDefTBO.getHandler())) {
            preprocessors = nkCustomObjectManager.getCustomObject(nkCardFileTemplateDefTBO.getHandler(), NkFileTemplateMetadataPreprocessor.class)
        }
        InputStream input = new ByteArrayInputStream(template)
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        try {
            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(input, TemplateEngineKind.Velocity)
            if (preprocessors) {
                preprocessors.processMeta(report)
            }
            IContext context = report.createContext()
            context.putMap(spELManager.getSpELMap())
            context.put("\$doc", doc)
            if (preprocessors) {
                preprocessors.processData(doc, context)
            }
            report.process(context, out)
            out.flush()
            return out.toByteArray()
        } catch (IOException | XDocReportException e) {
            throw new NkDefineException("文件导出模板错误", e)
        } finally {
            try {
                input.close()
                out.close()
            } catch (IOException e) {
                e.printStackTrace()
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class NkCardFileTemplateDefBO {
        List<NkCardFileTemplateDefTBO> items = new ArrayList<>()
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class NkCardFileTemplateDefTBO {
        String templateCode//模板项
        String templateDesc//描述
        String templateFileName//文件名称
        String templateBase64//base64的数据
        String exportType//导出类型，pdf还是word
        String handler//导出文件时的处理程序
    }
}
