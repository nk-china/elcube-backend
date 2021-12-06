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
    <nk-def-card :title="`${card.cardName}配置`">
        <nk-form :col="1" :edit="editMode">
            <nk-form-item title="服务">
                {{def.remoteAdapter}}
                <a-select v-model="def.remoteAdapter" slot="edit" size="small" style="width:200px" :options="adapters"></a-select>
            </nk-form-item>
            <nk-form-item title="服务参数">
                {{def.remoteAdapterParamsSpEL}}
                <nk-sp-el-template-editor  slot="edit" v-model="def.remoteAdapterParamsSpEL" style="width:200px"></nk-sp-el-template-editor>
            </nk-form-item>
        </nk-form>

        <vxe-table
                ref="xTable"
                row-key
                auto-resize
                size="mini"
                border=inner
                show-header-overflow="tooltip"
                show-overflow="tooltip"
                resizable
                highlight-hover-row
                :edit-config="{trigger: 'click', mode: 'row', showIcon: editMode}"
                :data="def.fields"
        >
            <vxe-table-column title="字段"
                              field="label"
                              width="12%"></vxe-table-column>
            <vxe-table-column title="可见"
                              field="visible"
                              width="8%"
                              :edit-render="{name:'$switch',props: {'open-value':true,'close-value':false}}"
                              :formatter="boolFormat"></vxe-table-column>
            <vxe-table-column title="只读"
                              field="readonly"
                              width="8%"
                              :edit-render="{name:'$switch',props: {'open-value':true,'close-value':false}}"
                              :formatter="boolFormat"></vxe-table-column>
            <vxe-table-column title="SpEL"
                              field="spELTriggers"
                              width="12%"
                              :edit-render="{name:'$select',props:{multiple:true},options:inputTriggers}"></vxe-table-column>
            <vxe-table-column title="SpEL值"
                              field="spELContent"
                              width="12%"
                              :edit-render="{}">
                <template v-slot="{row}">
                    {{row.spELContent}}
                </template>
                <template v-slot:edit="{row}">
                    <nk-sp-el-editor slot="edit" v-model="row.spELContent"></nk-sp-el-editor>
                </template>
            </vxe-table-column>
            <vxe-table-column title="SpEL顺序"
                              field="calcOrder"
                              width="12%"
                              :edit-render="{name:'$input', props: {type: 'number',min:1, max:4}}"></vxe-table-column>
            <vxe-table-column title="触发计算"
                              field="calcTrigger"
                              width="12%"
                              :edit-render="{name:'$switch',props: {'open-value':1,'close-value':0}}"
                              :formatter="boolFormat"></vxe-table-column>
            <vxe-table-column title="Options"
                              field="options"
                              width="12%"
                              :edit-render="{}">
                <template v-slot="{row}">
                    {{row.options}}
                </template>
                <template v-slot:edit="{row}">
                    <nk-sp-el-template-editor  v-if="row.type==='select'" slot="edit" v-model="row.options"></nk-sp-el-template-editor>
                    <span>{{row.spELOptions}}</span>
                </template>
            </vxe-table-column>
            <vxe-table-column />
        </vxe-table>
    </nk-def-card>
</template>

<script>
    import MixinDef from "MixinDef";

    export default {
        mixins:[new MixinDef({})],
        created() {

            console.log(this.def)

            if(!this.def.fields)
                this.def.fields = [];

            if(!this.def.fields.find(e=>e.key==='name'))
                this.def.fields.push({
                    key: 'name',
                    label: '姓名',
                    visible : true,
                    readonly : false,
                });

            if(!this.def.fields.find(e=>e.key==='sex'))
                this.def.fields.push({
                    key: 'sex',
                    label: '性别',
                    type: 'select',
                    visible : true,
                    readonly : false,
                });

            if(!this.def.fields.find(e=>e.key==='age'))
                this.def.fields.push({
                    key: 'age',
                    label: '年龄',
                    visible : true,
                    readonly : false,
                });

            if(!this.def.fields.find(e=>e.key==='likes'))
                this.def.fields.push({
                    key: 'likes',
                    label: '爱好',
                    visible : true,
                    readonly : true,
                });
        },
        data(){
            return {
                adapters:[],
                inputTriggers:[
                    {label:"ALWAYS",value:"ALWAYS"},
                    {label:"INIT",  value:"INIT"},
                    {label:"BLANK", value:"BLANK"},
                ]
            }
        },
        mounted(){
            this.nk$callDef("listedAdapters").then(res=>{
                this.adapters = res;
            });
        },
        methods:{
            boolFormat : ({cellValue})=>{return cellValue?'是':''},
        }
    }
</script>

<style scoped>

</style>