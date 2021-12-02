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
            <nk-form-item title="对话框选项" :col="2">
                {{value.options}}
                <nk-sp-el-template-editor slot="edit" v-model="value.options"></nk-sp-el-template-editor>
            </nk-form-item>
            <nk-form-item title="数据映射模版">
                {{value.dataMappings}}
                <nk-sp-el-template-editor slot="edit" v-model="value.dataMappings"></nk-sp-el-template-editor>
            </nk-form-item>
            <nk-form-item title="选项映射模版">
                <span style="width:100%;">{{value.optionMappings}}</span>
                <nk-sp-el-template-editor slot="edit" v-model="value.optionMappings"></nk-sp-el-template-editor>
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