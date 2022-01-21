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
package cn.nkpro.elcube.task.delegate;

import cn.nkpro.elcube.platform.gen.UserAccount;
import cn.nkpro.elcube.security.UserAuthorizationService;
import cn.nkpro.elcube.security.bo.UserGroupBO;
import lombok.Setter;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.el.FixedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("NkCountersignatureJavaDelegate")
public class NkCountersignatureJavaDelegate implements JavaDelegate {
    @Setter
    private FixedValue groupKey;
    @Setter
    private FixedValue groupExamine;
    @Autowired
    private UserAuthorizationService permService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String groupKeyValue = (String)this.groupKey.getValue(execution);
        String groupExamineValue = (String)this.groupExamine.getValue(execution);
        UserGroupBO groupDetail = this.permService.getGroupDetailByKey(groupKeyValue);
        List<UserAccount> accounts = groupDetail.getAccounts();
        List<String> collect = accounts.stream().map(UserAccount::getId).collect(Collectors.toList());
        execution.setVariableLocal(groupExamineValue, collect);
    }
}
