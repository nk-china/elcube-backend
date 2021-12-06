<!--
	This file is part of ELCard.
	ELCard is free software: you can redistribute it and/or modify
	it under the terms of the GNU Affero General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	ELCard is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Affero General Public License for more details.
	You should have received a copy of the GNU Affero General Public License
	along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
-->
<template>
    <div>
        <nk-form :edit="editMode" :col="2">
            <nk-form-item title="选项表达式">
                {{value.options}}
                <nk-sp-el-template-editor slot="edit" v-model="value.options"></nk-sp-el-template-editor>
            </nk-form-item>
            <nk-form-item title="选择模式">
                {{value.selectMode}}
                <a-select slot="edit" size="small" v-model="value.selectMode" mode="default" >
                    <a-select-option key="default">default</a-select-option>
                    <a-select-option key="multiple">multiple</a-select-option>
                    <a-select-option key="tags" disabled>tags</a-select-option>
                </a-select>
            </nk-form-item>
            <nk-form-item title="Tag颜色">
                {{value.tagColor}}
                <a-input slot="edit" v-model="value.tagColor"></a-input>
            </nk-form-item>
        </nk-form>
    </div>
</template>

<script>
export default {
    props:{
        editMode: Boolean,
        value: Object,
    }
}
</script>

<style scoped>

</style>