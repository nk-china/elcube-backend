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
package cn.nkpro.elcard.docengine.datasync.defaults;

import cn.nkpro.elcard.docengine.datasync.NkAbstractDocDataGeneralSyncAdapter;
import cn.nkpro.elcard.docengine.gen.DocDefDataSync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;


@Slf4j
@SuppressWarnings("all")
@Component("NkDocRestDataSync")
public class NkDocRestDataSyncImpl extends NkAbstractDocDataGeneralSyncAdapter<Map<String,Object>> {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    protected void doSyncSingle(Map<String,Object> singleData, DocDefDataSync def) {
        ResponseEntity<String> responseEntity = postJSON(def.getTargetArgs(), singleData, String.class);
        log.info(String.valueOf(responseEntity.getStatusCodeValue()));
        log.info(responseEntity.getStatusCode().toString());
        log.info(responseEntity.getBody());
    }

    @Override
    protected void doSyncMultiple(List multipleData, DocDefDataSync def) {
        ResponseEntity<String> responseEntity = postJSON(def.getTargetArgs(), multipleData, String.class);
        log.info(String.valueOf(responseEntity.getStatusCodeValue()));
        log.info(responseEntity.getStatusCode().toString());
        log.info(responseEntity.getBody());
    }

    protected <T> ResponseEntity<T> postUrlencoded(String url, Map<String,Object> keyValue,Class<T> responseType, Object... uriVariables){

        HttpHeaders headers = HttpHeaders.writableHttpHeaders(headers());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Map<String,Object>> entity = new HttpEntity<>(keyValue, headers);
        return restTemplate.postForEntity(url,entity,responseType,uriVariables);
    }

    protected <T> ResponseEntity<T> postJSON(String url, Object json, Class<T> responseType, Object... uriVariables){
        HttpHeaders headers = HttpHeaders.writableHttpHeaders(headers());

        HttpEntity<Object> entity = new HttpEntity<>(json, headers);
        return restTemplate.postForEntity(url,entity,responseType,uriVariables);
    }

    protected HttpHeaders headers(){
        return HttpHeaders.EMPTY;
    }
}
