<template>
    <nk-card>
        <vxe-toolbar v-if="editMode">
            <template v-slot:buttons>
                <vxe-button icon="fa fa-plus" status="perfect" size="mini" @click="xTableAdd()">新增</vxe-button>
            </template>
        </vxe-toolbar>
        <vxe-grid
                ref="xTable"
                row-key
                auto-resize
                keep-source
                resizable
                highlight-hover-row
                show-header-overflow="tooltip"
                show-overflow="tooltip"
                size="mini"
                border=inner
                :columns="tableColumns"
                :data="data"
                :loading="loading"
                :edit-rules="tableValidRules"
                :edit-config="{trigger: 'click', mode: 'row', showIcon: editMode, activeMethod: xTableActiveMethod, showStatus: true}"
                :sort-config="{trigger: 'cell', remote: false,showIcon: !editMode, defaultSort: {field: 'orderby', order: 'asc'}, orders: ['desc', 'asc', null]}">
        </vxe-grid>
        <nk-doc-select-modal v-model="docSelectModalVisible" :modal="docSelectModal" @select="xTableRefChanged"></nk-doc-select-modal>
    </nk-card>
</template>

<script>
import { Mixin,NkFormat } from "nk-ts5-platform";

const columnTypes = {
    text(field){
        return {
            field: field.key,
            title: field.name,
            width: (field.col || '10') + '%',
            editRender: field.readonly ? undefined : {
                name: '$input',
                props: {
                    type:       'text',
                    maxlength:  field.maxLength,
                },
                events : {
                    input:      this.xTableInputChanged,
                    prevNumber: this.xTableInputChanged,
                    nextNumber: this.xTableInputChanged,
                }
            }
        }
    },
    integer(field){

        return {
            field: field.key,
            title: field.name,
            width: (field.col || '10') + '%',
            formatter({cellValue}){
                return cellValue && NkFormat.nkNumber(cellValue,field.format)
            },
            editRender: field.readonly ? undefined : {
                name: '$input',
                props: {
                    type: 'integer',
                    min:   field.min,
                    max:   field.max,
                },
                events: {
                    input: this.xTableInputChanged,
                    prevNumber: this.xTableInputChanged,
                    nextNumber: this.xTableInputChanged,
                }
            }
        }
    },
    decimal(field){
        return {
            field: field.key,
            title: field.name,
            width: (field.col || '10') + '%',
            formatter({cellValue}){
                return cellValue && NkFormat.nkNumber(cellValue,field.format)
            },
            editRender: field.readonly ? undefined : {
                name: '$input',
                props: {
                    type:   'float',
                    min:    field.min,
                    max:    field.max,
                    step:   field.step | 0.01,
                    digits: field.digits | 2,
                },
                events: {
                    input: this.xTableInputChanged,
                    prevNumber: this.xTableInputChanged,
                    nextNumber: this.xTableInputChanged,
                }
            }
        }
    },
    percent(field){
        return {
            field: field.key,
            title: field.name,
            width: (field.col || '10') + '%',
            formatter({cellValue}){
                return cellValue && NkFormat.nkPercent(cellValue/100,field.format)
            },
            editRender: field.readonly ? undefined : {
                name: '$input',
                props: {
                    type: 'float',
                    min:    field.min,
                    max:    field.max,
                    step:   field.step | 0.01,
                    digits: field.digits | 2,
                },
                events: {
                    input: this.xTableInputChanged,
                    prevNumber: this.xTableInputChanged,
                    nextNumber: this.xTableInputChanged,
                }
            }
        }
    },
    date(field){
        return {
            field: field.key,
            title: field.name,
            width: (field.col || '10') + '%',
            formatter({cellValue}){
                return NkFormat.nkDatetimeISO(cellValue,field.format)
            },
            editRender: field.readonly ? undefined : {
                name: 'nkDate',
                props: {
                    type: 'date',
                },
                events: {
                    change: this.xTableValueChanged,
                }
            }
        }
    },
    datetime(field){
        return {
            field: field.key,
            title: field.name,
            width: (field.col || '10') + '%',
            formatter({cellValue}){
                return NkFormat.nkDatetimeISO(cellValue,field.format)
            },
            editRender: field.readonly ? undefined : {
                name: 'nkDateTime',
                props: {
                    type: 'datetime',
                },
                events: {
                    change: this.xTableValueChanged,
                }
            }
        }
    },
    switch(field){
        return {
            field: field.key,
            title: field.name,
            width: (field.col || '10') + '%',
            formatter({cellValue}){
                return cellValue === true ? (field.checked || 'YES') : (field.unChecked || 'NO')
            },
            editRender: field.readonly ? undefined : {
                name: '$switch',
                props: {},
                events: {
                    change: this.xTableValueChanged,
                }
            }
        }
    },
    select(field){
        let options = (field.options && JSON.parse(field.options))||[];
        return {
            field: field.key,
            title: field.name,
            width: (field.col || '10') + '%',
            formatter({cellValue}){
                if(cellValue){
                    return (Array.isArray(cellValue) ? cellValue : [cellValue]).map(item => {
                        let find = options && options.find(o => o.key === item || o.value === item);
                        if(find){
                            return find.label || find.name || find.title;
                        }
                        return item;
                    }).join(' , ');
                }
            },
            editRender: field.readonly ? undefined : {
                name: '$select',
                props: {
                    multiple: field.selectMode === 'multiple',
                    options
                },
                events: {
                    change: this.xTableValueChanged,
                }
            }
        }
    },
    ref(field){
        const self = this;
        const formatter = ({row, cellValue})=>{
            const info = row[field.key+'$doc'];
            return (info && info.docName) || cellValue;
        };
        const slots = {
            default(e,h){
                return [
                    h("nk-doc-link",{props:{doc:e.row[field.key+'$doc']}},formatter(e))
                ]
            }
        };

        if(!field.readonly){
            slots.edit = (e,h)=>{
                return [
                    h("span",formatter(e)),
                    h("a",{on:{click:()=>{self.refClick(e,field)}}},"选择")
                ]
            };
        }

        return {
            field: field.key,
            title: field.name,
            width: (field.col || '10') + '%',
            editRender: field.readonly ? undefined : {},
            slots
        }
    }
};

export default {
    mixins:[new Mixin()],
    created() {
    },
    data(){
        return {
            loading:false,
            docSelectModalVisible: false,
            docSelectModal:undefined,
            docSelectParams:undefined,
            docSelectItem: undefined
        }
    },
    computed:{
        tableValidRules(){
            const rules = {};
            if(this.editMode&&this.def&&this.def.items){
                this.def.items.forEach(field=>{
                    const list = [];
                    if(field.required){
                        list.push({ required: true,         message: field.message||'不能为空' });
                    }
                    if(field.min){
                        list.push({ min: field.min,         type:'number',message: field.message||`不能小于${field.min}` });
                    }
                    if(field.max){
                        list.push({ max: field.max,         type:'number',message: field.message||`不能大于${field.max}` });
                    }
                    if(field.minLength){
                        list.push({ min: field.minLength,   message: field.message||`字符长度不能小于${field.minLength}` });
                    }
                    if(field.maxLength){
                        list.push({ max: field.maxLength,   message: field.message||`字符长度不能小于${field.maxLength}` });
                    }
                    if(field.pattern){
                        list.push({ pattern: field.pattern, message: field.message||'校验不通过' });
                    }
                    rules[field.key]=list;
                });
            }
            return rules;
        },
        tableColumns(){
            if(this.def && this.def.items){

                const columns = this.def.items.map(
                    field=>
                        columnTypes[field.inputType].apply(this, [field])
                );

                if(!this.def.items.find(i=>i.key==='fieldKey')){
                    columns.splice(0,0,{
                        type:'seq',
                        title:'#',
                        width:'2%'
                    });
                }
                if(this.editMode){
                    columns.push({
                        title:"Action",
                        width:"10%",
                        "slots": {
                            default: ({row},h) => {
                                return [
                                    h(
                                        'span',
                                        {
                                            class:{'drag-btn':true},
                                            style:{"margin-right":"10px"}
                                        },
                                        [h('i',{class:{'vxe-icon--menu':true}})],
                                    ),
                                    h(
                                        'span',
                                        {on:{click:(e)=>{this.xTableRemoveRow(row,e);}}},
                                        [h('i',{class:{'vxe-icon--remove':true}})],
                                    )
                                ];
                            }
                        }
                    });
                }else{
                    columns.push({});
                }

                return columns;
            }


            return [];
        }
    },
    methods:{
        xTableActiveMethod(){
            return this.editMode;
        },
        xTableInputChanged(e,value){
            switch (e.column.editRender.name) {
                case "$input":
                    if(value.$event.type==='change' || value.$event.type==='mousedown')
                        this.xTableValueChanged(e,value);
                    break;
                case "$switch":
                    break;
            }
        },
        xTableValueChanged({column,row},{value}){
            if(!this.hasError()) {
                let item = this.def.items.find(i => i.key === column.property);
                if (item && item.calcTrigger) {
                    row[column.property] = value;
                    this.nk$calc();
                }
            }
        },
        xTableRefChanged(selected){
            const {row} = this.docSelectParams;
            if(row[this.docSelectItem.key]!==selected.docId){
                row[this.docSelectItem.key]=selected.docId;
                row[this.docSelectItem.key+'$doc']=selected;
                this.xTableValueChanged(this.docSelectParams,{value: selected.docId, $event:{type:"change"}});
            }
        },
        async xTableAdd(){
            let row = {};
            this.def.items.forEach(field=>{
                row[field.key]=undefined;
            });
            this.data.push(row);
            await this.$refs.xTable.loadData(this.data);
            await this.$refs.xTable.setActiveRow(row);
            await this.$refs.xTable.validate(row).catch(errMap => errMap)
        },
        xTableRemoveRow(row){
            this.data.splice(this.data.indexOf(row),1);
        },
        refClick(e,field){
            this.docSelectParams = e;
            this.docSelectItem = field;
            this.docSelectModal = (field.modal && JSON.parse(field.modal))||undefined;
            this.docSelectModalVisible = true;
        },

        itemChanged(value,item){
            if(!this.$refs.form.hasError()){
                if(item.calcTrigger){
                    this.nk$calc();
                }
            }
        },
        async hasError() {
            const fieldKey = this.def.items.find(i => i.key === 'fieldKey');
            if (fieldKey && NkUtil.isRepeat(this.data, ['fieldKey'])) {
                return `${fieldKey.name}重复，请检查后再次提交`;
            }
            const errMap = await this.$refs.xTable.fullValidate(true).catch(errMap => errMap)
            if (errMap) {
                return `${this.card.cardName}校验不通过`;
            }
            return false;
        }
    }
}
</script>

<style scoped>

</style>

<docs>
    - 这是一段文档
</docs>