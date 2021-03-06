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
package cn.nkpro.elcube.task;

import org.camunda.bpm.engine.impl.ProcessEngineImpl;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.util.SpringBootProcessEnginePlugin;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableProcessApplication
public class CamundaConfiguration extends SpringBootProcessEnginePlugin {

    @Override
    public void preInit(SpringProcessEngineConfiguration processEngineConfiguration) {
        super.preInit(processEngineConfiguration);
    }

    @Override
    public void postInit(SpringProcessEngineConfiguration processEngineConfiguration) {
        super.postInit(processEngineConfiguration);
    }

    @Override
    public void postProcessEngineBuild(ProcessEngineImpl processEngine) {
        super.postProcessEngineBuild(processEngine);
    }
}
