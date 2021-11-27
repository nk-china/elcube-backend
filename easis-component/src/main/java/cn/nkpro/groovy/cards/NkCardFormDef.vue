<template>
    <nk-def-card>

        <nk-form :col="1" :edit="editMode" style="width:300px;">
            <nk-form-item title="列">
                {{def.col}}
                <a-input v-model="def.col" slot="edit" size="small" />
            </nk-form-item>
        </nk-form>

        <vxe-toolbar v-if="editMode">
            <template v-slot:buttons>
                <vxe-button status="perfect" size="mini" @click="add()">新增</vxe-button>
                <vxe-button status="perfect" size="mini" @click="rowExpandAll(true)">全部展开</vxe-button>
                <vxe-button status="perfect" size="mini" @click="rowExpandAll()">全部收起</vxe-button>
            </template>
        </vxe-toolbar>
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
                :edit-config="{trigger: 'click', mode: 'row', showIcon: editMode, activeMethod}"
                :data="def.items"
                @toggle-row-expand="rowExpand"
        >
            <vxe-table-column title="KEY"
                              field="key"
                              width="17%"
                              :edit-render="{name:'$input',events:{change:keyChanged}}"></vxe-table-column>
            <vxe-table-column title="描述"
                              field="name"
                              width="17%"
                              :edit-render="{name:'$input'}"></vxe-table-column>
            <vxe-table-column title="输入框"
                              field="inputType"
                              width="17%"
                              :edit-render="{name:'$select',options:inputTypeDefs,events:{change:inputTypeChanged}}"></vxe-table-column>
            <vxe-table-column title="触发计算"
                              field="calcTrigger"
                              width="12%"
                              :edit-render="{name:'$switch',props: {'open-value':1,'close-value':0}}"
                              :formatter="boolFormat"></vxe-table-column>
            <vxe-table-column title="计算顺序"
                              field="calcOrder"
                              width="12%"
                              :edit-render="{name:'$input', props: {type: 'number',min:1, max:4}}"></vxe-table-column>
            <vxe-table-column title="列宽"
                              field="col"
                              width="10%"
                              :edit-render="{name:'$input', props: {type: 'number',min:1, max:4}}"></vxe-table-column>
            <vxe-table-column type="expand"
                              field="">
                <template v-slot="{seq,items}">
                    <span v-if="editMode && sortable" class="drag-btn" style="margin-left: 10px;">
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
                        <nk-form-item title="校验提示">
                            {{row.message}}
                            <a-input slot="edit" size="small" v-model="row.message"></a-input>
                        </nk-form-item>
                        <nk-form-item title="控制">
                            {{row.control===1 ?'读写':(row.control===0 ?'只读':'隐藏')}}
                            <a-select slot="edit" size="small" v-model="row.control" >
                                <a-select-option :key="1" >读写</a-select-option>
                                <a-select-option :key="0" >只读</a-select-option>
                                <a-select-option :key="-1">隐藏</a-select-option>
                            </a-select>
                        </nk-form-item>
                        <nk-form-item title="控制 SpEL 表达式">
                            {{row.spELControl}}
                            <nk-sp-el-editor slot="edit" v-model="row.spELControl"></nk-sp-el-editor>
                        </nk-form-item>
                        <nk-form-item title="值 SpEL 计算时点">
                            {{row.spELTriggers}}
                            <a-select slot="edit" size="small" v-model="row.spELTriggers" mode="multiple" >
                                <a-select-option key="ALWAYS">ALWAYS</a-select-option>
                                <a-select-option key="INIT">INIT</a-select-option>
                                <a-select-option key="BLANK">BLANK</a-select-option>
                            </a-select>
                        </nk-form-item>
                        <nk-form-item title="值 SpEL 表达式">
                            {{row.spELContent}}
                            <nk-sp-el-editor slot="edit" v-model="row.spELContent"></nk-sp-el-editor>
                        </nk-form-item>

                        <nk-form-item title="选项表达式" v-if="row.$options.options">
                            {{row.options}}
                            <nk-sp-el-template-editor slot="edit" v-model="row.options"></nk-sp-el-template-editor>
                        </nk-form-item>
                        <nk-form-item title="选择模式" v-if="row.$options.selectMode">
                            {{row.selectMode}}
                            <a-select slot="edit" size="small" v-model="row.selectMode" mode="default" >
                                <a-select-option key="default">default</a-select-option>
                                <a-select-option key="multiple">multiple</a-select-option>
                                <a-select-option key="tags" disabled>tags</a-select-option>
                            </a-select>
                        </nk-form-item>
                        <nk-form-item title="Min" v-if="row.$options.min!==undefined">
                            {{row.min}}
                            <a-input-number slot="edit" size="small" v-model="row.min"></a-input-number>
                        </nk-form-item>
                        <nk-form-item title="Max" v-if="row.$options.max">
                            {{row.max}}
                            <a-input-number slot="edit" size="small" v-model="row.max"></a-input-number>
                        </nk-form-item>
                        <nk-form-item title="MinLength" v-if="row.$options.minLength">
                            {{row.minLength}}
                            <a-input-number slot="edit" size="small" :min="0" :max="200" :precision="0" v-model="row.minLength"></a-input-number>
                        </nk-form-item>
                        <nk-form-item title="MaxLength" v-if="row.$options.maxLength">
                            {{row.maxLength}}
                            <a-input-number slot="edit" size="small" :min="1" :max="200" :precision="0" v-model="row.maxLength"></a-input-number>
                        </nk-form-item>
                        <nk-form-item title="Digits" v-if="row.$options.digits">
                            {{row.digits}}
                            <a-input-number slot="edit" size="small" :min="0" :max="6" :precision="0" v-model="row.digits"></a-input-number>
                        </nk-form-item>
                        <nk-form-item title="Step" v-if="row.$options.step">
                            {{row.step}}
                            <a-input-number slot="edit" size="small" :min="0.0001" :max="10000" :precision="2" v-model="row.step"></a-input-number>
                        </nk-form-item>
                        <nk-form-item title="选中后显示" v-if="row.$options.checked">
                            {{row.checked}}
                            <a-input slot="edit" size="small" :maxLength="20" v-model="row.checked"></a-input>
                        </nk-form-item>
                        <nk-form-item title="未选中显示" v-if="row.$options.unChecked">
                            {{row.unChecked}}
                            <a-input slot="edit" size="small" :maxLength="20" v-model="row.unChecked"></a-input>
                        </nk-form-item>
                        <nk-form-item title="正则表达式" v-if="row.$options.pattern !== undefined">
                            {{row.pattern}}
                            <a-input slot="edit" size="small" :maxLength="20" v-model="row.pattern"></a-input>
                        </nk-form-item>
                        <nk-form-item title="对话框" v-if="row.$options.modal !== undefined">
                            {{row.modal}}
                            <nk-doc-select-editor slot="edit" v-model="row.modal"></nk-doc-select-editor>
                        </nk-form-item>
                        <nk-form-item title="显示格式" v-if="row.$options.format">
                            {{row.format}}
                            <a-input slot="edit" size="small" v-model="row.format"></a-input>
                        </nk-form-item>
                    </nk-form>
                </template>
            </vxe-table-column>
        </vxe-table>
    </nk-def-card>
</template>

<script>
    import {MixinDef, MixinSortable} from "nk";

    const inputTypeDefs = [
    {label:'文本 | text',    value:'text',     options:{                            minLength:0, maxLength:200,pattern:''                     }},
    {label:'整数 | integer', value:'integer',  options:{format:'#',                 min:0, max:2147483647                                     }},
    {label:'小数 | decimal', value:'decimal',  options:{format:'#.00',              min:0, max:2147483647, digits:2, step:0.02                }},
    {label:'比例 | percent', value:'percent',  options:{format:'#.00',              min:0, max:100,        digits:2, step:0.02                }},
    {label:'日期 | date',    value:'date',     options:{format:'YYYY/M/D',          min:0, max:4105094400                                     }},
    {label:'时间 | datetime',value:'datetime', options:{format:'YYYY/M/D HH:mm:ss', min:0, max:4105094400                                     }},
    {label:'开关 | switch',  value:'switch',   options:{                            checked:'是',unChecked:'否'                               }},
    {label:'选择 | select',  value:'select',   options:{                                                   options:'[]',selectMode:'default'  }},
    {label:'级联 | cascader',value:'cascader', options:{                                                   options:'[]'                       }},
    {label:'树形 | tree',    value:'tree',     options:{                                                   options:'[]'                       }},
    {label:'引用 | ref',     value:'ref',      options:{                                                   modal:''                           }},
    {label:'分隔 | divider', value:'divider',  options:{                                                                                      }},
];

export default {
    mixins:[new MixinDef({col:1,items:[]}),MixinSortable()],
    data(){
        return {
            inputTypeDefs,
            sortable:true,
            modalRow:{},
            modalVisible:false
        }
    },
    created() {
        this.$nkSortableVxeTable(true);
        if(!this.def.items){
            this.$set(this.def,'items',[]);
        }
        this.nk$callDef(1);
    },
    methods:{
        boolFormat : ({cellValue})=>{return cellValue?'是':''},
        activeMethod(){return this.editMode;},
        initRowOptions(row){
            if(row.inputType){
                row.$options = inputTypeDefs.find(e=>e.value===row.inputType).options
                for(let key in row.$options){
                    if(!row[key]){
                        row[key]=row.$options[key];
                    }
                }
            }
        },
        rowExpand({expanded,row}){
            if(expanded){
                this.initRowOptions(row);
            }
            this.sortable = this.$refs.xTable.getRowExpandRecords().length === 0;
            this.$nkSortableVxeTable(this.sortable);
        },
        rowExpandAll(bool){
            if(bool){
                this.def.items.forEach((row)=>{
                    this.initRowOptions(row);
                });
                this.$refs.xTable.setAllRowExpand(bool);
            }else{
                this.$refs.xTable.clearRowExpand();
            }
            this.sortable = this.$refs.xTable.getRowExpandRecords().length === 0;
            this.$nkSortableVxeTable(this.sortable);
        },
        keyChanged({column,row},{value}){
            row[column.property]=value && value.toUpperCase()
        },
        inputTypeChanged({row}){
            row.$options = inputTypeDefs.find(e=>e.value===row.inputType).options;
            for(let key in row.$options){
                this.$set(row,key,row.$options[key]);
            }
        },
        add(){
            let newItem = {
                key:'KEY',
                name:'新字段',
                col:1,
                inputType:'text',
                calcTrigger:'',
                calcOrder:1,
                required:true,
                control:1,
                spELContent:'',
                spELTriggers:[],
                eval:''
            };
            this.def.items.push(newItem);
            this.$refs.xTable.loadData(this.def.items).then(() => this.$refs.xTable.setActiveRow(newItem));
        },
        refClick(row){
            this.modalRow = row;
            this.modalVisible = true;
        }
    }
}
</script>

<style scoped>

</style>

<docs>
    -- doc
</docs>