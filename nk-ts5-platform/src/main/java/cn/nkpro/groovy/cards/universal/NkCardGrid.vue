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
                size="mini"
                border=inner
                :columns="tableColumns"
                :data="data"
                :loading="loading"
                :edit-rules="tableValidRules"
                :edit-config="{trigger: 'click', mode: 'row', showIcon: editMode, activeMethod: xTableActiveMethod, showStatus: true}"
                :sort-config="{trigger: 'cell', remote: false,showIcon: !editMode, defaultSort: {field: 'orderby', order: 'asc'}, orders: ['desc', 'asc', null]}">
        </vxe-grid>
        <nk-doc-select-modal v-model="docSelectModalVisible" :modal="docSelectModal" @select="docSelected"></nk-doc-select-modal>
    </nk-card>
</template>

<script>
import { Mixin } from "nk-ts5-platform";
import moment from "moment";
import { Interpreter } from "eval5";
import { TreeSelect } from 'ant-design-vue';

const editRenders = {
    text(){
        return{
            name: '$input',
            props: {
                type: 'text',
                maxlength:200
            }
        }
    },
    integer(){
        return{
            name: '$input',
            props: {
                type: 'integer',
            }
        }
    },
    decimal(){
        return{
            name: '$input',
            props: {
                type: 'float',
                digits: 2,
            }
        }
    },
    percent(){
        return{
            name: '$input',
            props: {
                type: 'float',
                digits:2
            },
        }
    },
    date(){
        return{
            name: 'nkDate',
            props: {
                size: 'small',
                type: 'date',
                format: 'YYYY/MM/DD'
            }
        }
    },
    datetime(){
        return{
            name: 'nkDateTime',
            props: {
                size: 'small',
                type: 'datetime',
                format: 'YYYY/MM/DD HH:mm:ss'
            }
        }
    },
    switch(){
        return{
            name: '$switch',
            props: {
            }
        }
    },
    select(field){
        console.log(field)
        return {
            name: '$select',
            props: {
                multiple: field.selectMode === 'multiple',
                options: JSON.parse(field.options)
            }
        }
    },
    ref(){
        return {
            name: '$input',
            props: {
                readonly:true
            }
        }
    }
};

const formatters = {

};

const findInTree = (tree,value)=>{
    for(let i in tree){
        const item = tree[i];
        if(item.key === value || item.value === value){
            return item;
        }
        if(item.children){
            let ret = findInTree(item.children, value);
            if(ret){
                return ret;
            }
        }
    }
    return undefined;
};

export default {
    mixins:[new Mixin()],
    filters:{
        formatRef(value, options){
            const info = value[options.key+'$doc'];
            return (info && info.docName) || value[options.key];
        },
        formatSwitch(value, options) {
            return value === true ? (options.checked || 'YES') : (options.unChecked || 'NO');
        },
        formatSelect(value, options) {
            if(value){
                options = typeof options === 'string' ? JSON.parse(options) : options;
                return (Array.isArray(value) ? value : [value]).map(item => {
                    let find = options && options.find(o => o.key === item || o.value === item);
                    if(find){
                        return find.label || find.name || find.title;
                    }
                    return item;
                }).join(' , ');
            }
        },
        formatCascader(value, options){
            if(value) {
                options = typeof options === 'string' ? JSON.parse(options) : options;
                let find = options;
                return value.map(item => {
                    find = find && find.find(o => o.key === item || o.value === item);
                    if (find) {
                        const label = find.label || find.name || find.title;
                        find = find.children;
                        return label;
                    }
                    return item;
                }).join(' / ');
            }
        },
        formatTree(value, options){
            if(value) {
                options = typeof options === 'string' ? JSON.parse(options) : options;
                return (Array.isArray(value) ? value : [value]).map(item => {
                    let find = options && findInTree(options, item);
                    if (find) {
                        return find.label || find.name || find.title;
                    }
                    return item;
                }).join(' , ');
            }
        }
    },
    created() {
    },
    data(){
        return {
            loading:false,
            SHOW_PARENT:TreeSelect.SHOW_PARENT,
            docSelectModalVisible: false,
            docSelectModal:undefined,
            docSelectRow:undefined,
            docSelectItem: undefined
        }
    },
    computed:{
        tableColumns(){
            if(this.def && this.def.items){

                let self = this;

                const columns = this.def.items.map(field=>{

                    let editRender = editRenders[field.inputType](field);

                    if(editRender.name==='$input'){
                        editRender.events = {
                            input:      this.xTableInputChanged,
                            prevNumber: this.xTableInputChanged,
                            nextNumber: this.xTableInputChanged,
                        };
                    }
                    if(editRender.name==='nkDate'||editRender.name==='nkDateTime'){
                        editRender.events = {
                            change:    this.xTableDateChanged,
                        };
                    }

                    let slots = undefined;
                    if(field.inputType==='ref'){
                        field.readonly = true;
                        slots = {
                            edit(e,h){
                                return [
                                    h("a",{on:{click:()=>{self.refClick(e,field)}}},"选择")
                                ]
                            }
                        };
                    }


                    return {
                        field:field.key,
                        title:field.name,
                        width:field.width,
                        //formatter:formatters[field.inputType](field.format,editRender.props),
                        editRender:field.readonly!==1?editRender:undefined,
                        slots
                    }
                });
                if(!this.def.items.find(i=>i.key==='fieldKey')){
                    columns.splice(0,0,{
                        type:'seq',
                        title:'#',
                        width:'40px'
                    });
                }
                if(this.editMode){
                    columns.push({
                        title:"Action",
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
        },
        tableValidRules(){
            return {};
        }
    },
    methods:{
        xTableActiveMethod(){
            return this.editMode;
        },
        xTableInputChanged({column},{value,$event}){
            switch (column.editRender.name) {
                case "$input":
                    if($event.type==='change' || $event.type==='mousedown')
                        console.log(value);
                    break;
                case "$switch":
                    break;
            }
        },
        xTableDateChanged(a,b){
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
        refClick({row},field){
            this.docSelectRow = row;
            this.docSelectItem = field;
            this.docSelectModal = (field.modal && JSON.parse(field.modal))||undefined;
            this.docSelectModalVisible = true;
        },
        docSelected(selected){
            this.docSelectRow[this.docSelectItem.key]=selected.docId;
            this.docSelectRow[this.docSelectItem.key+'$doc']=selected;
        },




        moment,
        percentParse(value){
            return value && value.replace(/[,%]/, '');
        },
        percentFormat(value){
            return value && (value+'%');
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
            const errMap = await this.$refs.xTable.fullValidate().catch(errMap => errMap)
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