/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.co.fs;

import cn.nkpro.elcard.basic.NkProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FSSupport<T> {

    default String defaultPath(String rootPath, NkProperties tfmsProperties){
        String path = rootPath + tfmsProperties.getEnvKey();
        path += path.endsWith("/")?(".nk_temp/"):("/.nk_temp/");
        path += UUID.randomUUID().toString()+'/';
        return path;
    }

    /**
     * 初始化上传配置
     * @return
     */
    ResponseEntity<T> init(FSConfig file);

    /**
     * 上传文件到临时文件夹
     * @param file 文件
     * @return 上传结果
     */
    FileUploadStatus upload(MultipartFile file);

    /**
     * 将文件从临时目录转移到正式目录
     * @param source
     * @param target
     */
    void moveTo(String source,String target);

    /**
     * 获取下载链接
     * @param url
     * @return
     */
    ResponseEntity download(String url);
}
