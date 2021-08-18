<template>
    <a-card :title="`${card.cardName}配置`">
        <nk-form :col="1" :edit="editMode" style="width:300px;">
            <nk-form-item title="列">
                {{def.col}}
                <a-input v-model="def.col" slot="edit" />
            </nk-form-item>
        </nk-form>

        <vxe-toolbar v-if="editMode">
            <template v-slot:buttons>
                <vxe-button icon="fa fa-plus" status="perfect" size="mini" @click="add()">新增</vxe-button>
            </template>
        </vxe-toolbar>
        <vxe-table
                ref="xTable"
                row-key
                auto-resize
                size="mini"
                border=inner
                show-overflow="tooltip"
                resizable
                highlight-hover-row
                :edit-config="{trigger: 'click', mode: 'row', showIcon: editMode, activeMethod}"
                :data="def.items"
                @toggle-row-expand="rowExpand"
        >
            <vxe-table-column title="KEY"
                              field="key"
                              width="15%"
                              :edit-render="{name:'$input',events:{change:keyChanged}}"></vxe-table-column>
            <vxe-table-column title="描述"
                              field="name"
                              width="15%"
                              :edit-render="{name:'$input'}"></vxe-table-column>
            <vxe-table-column title="输入框"
                              field="inputType"
                              width="15%"
                              :edit-render="{name:'$select',options:inputTypeDefs,events:{change:inputTypeChanged}}"></vxe-table-column>
            <vxe-table-column title="触发计算"
                              field="calcTrigger"
                              width="8%"
                              :edit-render="{name:'$switch',props: {'open-value':1,'close-value':0}}"
                              :formatter="boolFormat"></vxe-table-column>
            <vxe-table-column title="计算顺序"
                              field="calcOrder"
                              width="10%"
                              :edit-render="{name:'$input', props: {type: 'number',min:1, max:4}}"></vxe-table-column>
            <vxe-table-column title="列宽"
                              field="col"
                              width="10%"
                              :edit-render="{name:'$input', props: {type: 'number',min:1, max:4}}"></vxe-table-column>
            <vxe-table-column type="expand"
                              field=""
                              width="10%">
                <template v-slot="{seq,items}">
                    <span v-if="editMode" class="drag-btn" style="margin-left: 10px;">
                        <i class="vxe-icon--menu"></i>
                    </span>
                    <span v-if="editMode" style="margin-left: 10px;" @click="$nkSortableRemove(def.items,seq)">
                        <i class="vxe-icon--remove"></i>
                    </span>
                </template>
                <template #content="{ row }">
                    <nk-form :edit="editMode" :col="2">
                        <nk-form-item title="是否非空">
                            {{row.required?'是':'否'}}
                            <a-switch slot="edit" size="small" v-model="row.required" />
                        </nk-form-item>
                        <nk-form-item title="是否只读">
                            {{row.readonly?'是':'否'}}
                            <a-switch slot="edit" size="small" v-model="row.readonly" />
                        </nk-form-item>
                        <nk-form-item title="SpEL 表达式">
                            {{row.spELContent}}
                            <a-input slot="edit" size="small" v-model="row.spELContent"></a-input>
                        </nk-form-item>
                        <nk-form-item title="SpEL 计算时点">
                            {{row.spELTriggers}}
                            <a-select slot="edit" size="small" v-model="row.spELTriggers" mode="multiple" >
                                <a-select-option key="ALWAYS">ALWAYS</a-select-option>
                                <a-select-option key="INIT">INIT</a-select-option>
                                <a-select-option key="BLANK">BLANK</a-select-option>
                            </a-select>
                        </nk-form-item>
                        <nk-form-item title="JS Eval">
                            {{row.eval}}
                            <a-input slot="edit" size="small" v-model="row.eval"></a-input>
                        </nk-form-item>

                        <nk-form-item title="显示格式" v-if="row.$options.format">
                            {{row.format}}
                            <a-input slot="edit" size="small" v-model="row.format"></a-input>
                        </nk-form-item>
                        <nk-form-item title="选项表达式" v-if="row.$options.options">
                            {{row.options}}
                            <a-input slot="edit" size="small" v-model="row.options"></a-input>
                        </nk-form-item>
                        <nk-form-item title="条件" v-if="row.$options.conditions">
                            {{row.conditions}}
                            <a-input slot="edit" size="small" v-model="row.conditions"></a-input>
                        </nk-form-item>
                        <nk-form-item title="Min" v-if="row.$options.min!==undefined">
                            {{row.min}}
                            <a-input slot="edit" size="small" v-model="row.min"></a-input>
                        </nk-form-item>
                        <nk-form-item title="Max" v-if="row.$options.max">
                            {{row.max}}
                            <a-input slot="edit" size="small" v-model="row.max"></a-input>
                        </nk-form-item>
                        <nk-form-item title="MaxLength" v-if="row.$options.maxLength">
                            {{row.maxLength}}
                            <a-input slot="edit" size="small" v-model="row.maxLength"></a-input>
                        </nk-form-item>
                    </nk-form>
                </template>
            </vxe-table-column>
        </vxe-table>
    </a-card>
</template>

<script>
import { MixinDef, MixinSortable } from "nk-ts5-platform";

const inputTypeDefs = [
    {label:'文本 | text',    value:'text',     options:{                            maxLength:200                          }},
    {label:'整数 | integer', value:'integer',  options:{format:'#',                 min:0, max:2147483647                  }},
    {label:'小数 | decimal', value:'decimal',  options:{format:'#.00',              min:0, max:2147483647                  }},
    {label:'比例 | percent', value:'percent',  options:{format:'#.00',              min:0, max:100                         }},
    {label:'日期 | date',    value:'date',     options:{format:'yyyy/M/d',          min:0, max:4105094400                  }},
    {label:'时间 | datetime',value:'datetime', options:{format:'yyyy/M/d HH:mm:ss', min:0, max:4105094400                  }},
    {label:'单选 | select',  value:'select',   options:{                                                   options:'[]'    }},
    {label:'多选 | multiple',value:'multiple', options:{                                                   options:'[]'    }},
    {label:'级联 | cascader',value:'cascader', options:{                                                   options:'[]'    }},
    {label:'树形 | tree',    value:'tree',     options:{                                                   options:'[]'    }},
    {label:'引用 | ref',     value:'ref',      options:{                                                   conditions:'{}' }},
    {label:'分隔 | divider', value:'divider',  options:{                                                                   }},
];

export default {
    mixins:[new MixinDef({col:1,items:[]}),MixinSortable()],
    data(){
        return {
            inputTypeDefs
        }
    },
    created() {
        this.$nkSortableVxeTable(true);
    },
    methods:{
        boolFormat : ({cellValue})=>{return cellValue?'是':''},
        activeMethod(){return this.editMode;},
        rowExpand({expanded,row}){
            if(expanded && row.inputType){
                row.$options = inputTypeDefs.find(e=>e.value===row.inputType).options
                for(let key in row.$options){
                    if(!row[key]){
                        row[key]=row.$options[key];
                    }
                }
            }
        },
        keyChanged({column,row},{value}){
            row[column.property]=value && value.toUpperCase()
        },
        inputTypeChanged({row}){
            row.$options = inputTypeDefs.find(e=>e.value===row.inputType).options;
            Object.assign(row,row.$options)
        },
        add(){
            let newItem = {
                key:'KEY',
                name:'新字段',
                inputType:'text',
                calcTrigger:'',
                calcOrder:1,
                required:true,
                readonly:false,
                eval:'',
                spELContent:'',
                spELTriggers:['ALWAYS','INIT','BLANK'],
                col:1
            };
            this.def.items.push(newItem);
            this.$refs.xTable.loadData(this.def.items).then(() => this.$refs.xTable.setActiveRow(newItem));
        },
        switchChanged(row,key,e){
            row[key]=e
        }
    }
}
</script>

<style scoped>

</style>