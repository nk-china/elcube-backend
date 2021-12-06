<!--
	This file is part of ELCube.
	ELCube is free software: you can redistribute it and/or modify
	it under the terms of the GNU Affero General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	ELCube is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Affero General Public License for more details.
	You should have received a copy of the GNU Affero General Public License
	along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
-->
<template>
    <div>
        <nk-form :edit="editMode" :col="2">
            <nk-form-item title="基本值表达式" :col="2">
                {{value.baseSpEL}}
                <nk-sp-el-editor slot="edit" v-model="value.baseSpEL"></nk-sp-el-editor>
            </nk-form-item>
            <nk-form-item title="百分比格式">
                {{value.format}}
                <a-input size="small" slot="edit" v-model="value.format"></a-input>
            </nk-form-item>
            <nk-form-item title="值格式">
                {{value.valueFormat}}
                <a-input size="small" slot="edit" v-model="value.valueFormat"></a-input>
            </nk-form-item>
        </nk-form>
        <span style="padding-left: 40px">
            支持的Mapping：
            <code>
                <pre style="padding-left: 40px;">
{
    options: ["double"], // 百分比选项，该值一档设置，min及max失效，且值只读
    min: "double",// 最小百分比
    max: "double",// 最大百分比
    valueMin: "double", // 最小值
    valueMax: "double", // 最大值
    defaultPercent: "double", // 默认百分比
    defaultValue: "double", // 默认值
}
                </pre>
            </code>
        </span>
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