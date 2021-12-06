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
package cn.nkpro.elcube.exception.abstracts;

/**
 * 警告
 */
public abstract class NkCaution extends RuntimeException {

    public NkCaution(String message) {
        super(message);
    }

    public NkCaution(Throwable cause) {
        super(cause);
    }

    public NkCaution(String message, Throwable cause) {
        super(message, cause);
    }
}
