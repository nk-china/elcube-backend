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
package cn.nkpro.elcube.co;

import cn.nkpro.elcube.exception.abstracts.NkRuntimeException;
import lombok.Getter;

/**
 *
 * 组件运行时异常：卡片，脚本等
 * Created by bean on 2020/1/15.
 */
public class NkComponentException extends NkRuntimeException {

    @Getter
    private NkScriptComponent component;

    public NkComponentException(NkScriptComponent component, Throwable cause) {
        super(String.format("组件[%s]发生错误：%s",component.getName(),cause.getMessage()), cause);
        this.component = component;
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }
}
