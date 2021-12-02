<!--
	This file is part of EAsis.
	EAsis is free software: you can redistribute it and/or modify
	it under the terms of the GNU Affero General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	EAsis is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Affero General Public License for more details.
	You should have received a copy of the GNU Affero General Public License
	along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
-->
<template>
    <div>
        <nk-form :edit="editMode" :col="2">
            <nk-form-item title="类型">
                {{value.listType}}
                <a-select slot="edit" size="small" v-model="value.listType" >
                    <a-select-option key="text" >文件</a-select-option>
                    <a-select-option key="picture-card" >图片</a-select-option>
                </a-select>
            </nk-form-item>
            <nk-form-item title="Accept">
                {{value.accept}}
                <a-input size="small" slot="edit" v-model="value.accept"></a-input>
            </nk-form-item>
            <nk-form-item title="MaxSize(KB)">
                {{value.maxSize}}
                <a-input-number size="small" slot="edit" v-model="value.maxSize" :max="1024*10"></a-input-number>
            </nk-form-item>
        </nk-form>
    </div>
</template>

<script>
export default {
    props:{
        editMode: Boolean,
        value: Object
    }
}
</script>

<style scoped>

</style>