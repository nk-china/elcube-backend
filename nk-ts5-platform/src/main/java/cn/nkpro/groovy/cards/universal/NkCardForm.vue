<template>
    <nk-card>
        <nk-form ref="form" :col="def.col" :edit="editMode" >
            <template v-for="(item,index) in def.items" >
                <nk-form-divider
                        v-if="item.control >= 0 && item.inputType==='divider'"
                        :key="index"
                        :term="item.name"></nk-form-divider>
                <nk-form-item
                        v-else-if="item.control >= 0"
                        :edit="item.control === 1"
                        :key="item.key"
                        :term="item.name"
                        :col="item.col"
                        :validateFor="data[item.key]"
                        :required="!!item.required"
                        :len="(!!item.maxLength)||(!!item.minLength)"
                        :min="item.min || item.minLength"
                        :max="item.max || item.maxLength"
                        :pattern="item.pattern"
                        :message="item.message || ('\''+item.name+'\'校验不通过')"
                >
                    <template v-if     ="item.inputType==='integer' || item.inputType==='decimal'">
                        {{data[item.key] | nkNumber(item.format)}}
                    </template>
                    <template v-else-if     ="item.inputType==='percent'">
                        {{data[item.key]/100 | nkPercent(item.format)}}
                    </template>
                    <template v-else-if     ="item.inputType==='date' || item.inputType==='datetime'">
                        {{data[item.key] | nkDatetime(item.format)}}
                    </template>
                    <template v-else-if     ="item.inputType==='switch'">
                        {{data[item.key] | formatSwitch(item)}}
                    </template>
                    <template v-else-if     ="item.inputType==='select'">
                        {{data[item.key] | formatSelect(item.optionsObject)}}
                    </template>
                    <template v-else-if     ="item.inputType==='cascader'">
                        {{data[item.key] | formatCascader(item.optionsObject)}}
                    </template>
                    <template v-else-if     ="item.inputType==='tree'">
                        {{data[item.key] | formatTree(item.optionsObject)}}
                    </template>
                    <template v-else-if     ="item.inputType==='ref'">
                        <nk-doc-link :doc="data[item.key+'$doc']">{{data | formatRef(item)}}</nk-doc-link>
                    </template>
                    <template v-else>
                        {{data[item.key]}}
                    </template>

                    <a-input        v-if            ="item.inputType==='text'"
                                    slot            ="edit"
                                    size            ="small"
                                    style           ="max-width: 300px;"
                                    v-model         ="data[item.key]"
                                    :maxLength      ="item.maxLength"
                                    @blur           ="itemChanged($event,item)"></a-input>
                    <a-input-number v-else-if       ="item.inputType==='integer'"
                                    slot            ="edit"
                                    size            ="small"
                                    style           ="max-width: 300px;"
                                    :style          ="item.style"
                                    v-model         ="data[item.key]"
                                    :min            ="item.min"
                                    :max            ="item.max"
                                    :precision      ="0"
                                    :step           ="item.step||1"
                                    @blur           ="itemChanged($event,item)"/>
                    <a-input-number v-else-if       ="item.inputType==='decimal'"
                                    slot            ="edit"
                                    size            ="small"
                                    style           ="max-width: 300px;"
                                    :style          ="item.style"
                                    v-model         ="data[item.key]"
                                    :min            ="item.min"
                                    :max            ="item.max"
                                    :precision      ="item.digits||2"
                                    :step           ="item.step||0.01"
                                    @blur           ="itemChanged($event,item)"/>
                    <a-input-number v-else-if       ="item.inputType==='percent'"
                                    slot            ="edit"
                                    size            ="small"
                                    style           ="max-width: 300px;"
                                    :style          ="item.style"
                                    v-model         ="data[item.key]"
                                    :min            ="item.min"
                                    :max            ="item.max"
                                    :precision      ="item.digits||2"
                                    :step           ="item.step||0.01"
                                    @blur           ="itemChanged($event,item)"
                                    :formatter      ="percentFormat"
                                    :parser         ="percentParse"
                    />
                    <a-date-picker  v-else-if       ="item.inputType==='date'"
                                    slot            ="edit"
                                    size            ="small"
                                    :style          ="item.style"
                                    :defaultValue   ="data[item.key]?moment(data[item.key]*1000):null"
                                    @change         ="dateChanged($event,item)"></a-date-picker>
                    <a-date-picker  v-else-if       ="item.inputType==='datetime'"
                                    show-time
                                    slot            ="edit"
                                    size            ="small"
                                    :style          ="item.style"
                                    :defaultValue   ="data[item.key]?moment(data[item.key]*1000):null"
                                    @change         ="datetimeChanged($event,item)"></a-date-picker>
                    <a-switch       v-else-if       ="item.inputType==='switch'"
                                    slot            ="edit"
                                    v-model         ="data[item.key]"
                                    size            ="small"
                                    @change         ="itemChanged($event,item)">
                    </a-switch>
                    <a-select       v-else-if       ="item.inputType==='select'"
                                    slot            ="edit"
                                    :mode            ="item.selectMode||'default'"
                                    v-model         ="data[item.key]"
                                    size            ="small"
                                    style           ="max-width: 250px;"
                                    :style          ="item.style"
                                    @change         ="itemChanged($event,item)"
                                    :options        ="item.optionsObject">
                    </a-select>
                    <a-cascader     v-else-if       ="item.inputType==='cascader'"
                                    slot            ="edit"
                                    size            ="small"
                                    v-model         ="data[item.key]"
                                    style           ="max-width: 250px;"
                                    :style          ="item.style"
                                    @change         ="itemChanged($event,item)"
                                    :options        ="item.optionsObject">
                    </a-cascader>
                    <a-tree-select  v-else-if       ="item.inputType==='tree'"
                                    slot            ="edit"
                                    v-model         ="data[item.key]"
                                    style           ="max-width: 350px;"
                                    :style          ="item.style"
                                    size            ="small"
                                    :tree-data      ="item.optionsObject"
                                    tree-checkable
                                    :show-checked-strategy="SHOW_PARENT"
                                    search-placeholder="Please select"
                                    @change="itemChanged($event,item)"
                    />
                    <label          v-else-if       ="item.inputType==='ref'"
                                    slot            ="edit"
                                    class           ="ref-input ant-input-affix-wrapper"
                                    style           ="max-width: 250px;"
                                    :style          ="item.style"
                    >
                        <input      class="ant-input ant-input-sm"
                                    :value="data | formatRef(item)"
                                    style="cursor: pointer;"
                                    readonly
                                    @click="refClick(item)" />
                        <span       class="ant-input-suffix">
                            <a-icon type="select" style="color: rgba(0,0,0,.45)" />
                        </span>
                    </label>
                </nk-form-item>
            </template>
        </nk-form>
        <nk-doc-select-modal v-model="docSelectModalVisible" :modal="docSelectModal" @select="docSelected"></nk-doc-select-modal>
    </nk-card>
</template>

<script>
import { Mixin } from "nk-ts5-platform";
import moment from "moment";
import { Interpreter } from "eval5";
import { TreeSelect } from 'ant-design-vue';

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
        formatRef(value, field){
            const info = value[field.key+'$doc'];
            return (info && info.docName) || value[field.key];
        },
        formatSwitch(value, field) {
            return value === true ? (field.checked || 'YES') : (field.unChecked || 'NO');
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
        this.def.items.forEach(field=>{
            if(this.data[field.key]===undefined){
                if(field.inputType==='switch'){
                    this.$set(this.data,field.key,true);
                }else if(field.inputType==='select' && field.selectMode==='multiple'){
                    this.$set(this.data,field.key,[]);
                }else{
                    this.$set(this.data,field.key,null);
                }
            }
        })
    },
    data(){
        return {
            SHOW_PARENT:TreeSelect.SHOW_PARENT,
            docSelectModalVisible: false,
            docSelectModal:undefined,
            docSelectItem: undefined
        }
    },
    computed:{
    },
    methods:{
        moment,
        percentParse(value){
            return value && value.replace(/[,%]/, '');
        },
        percentFormat(value){
            return value && (value+'%');
        },
        itemChanged(value,item){
            if(item.calcTrigger){
                this.nk$calc();
            }
        },
        dateChanged(value,item){
            value = value && value.startOf('day').unix();
            this.data[item.key]=value;
            this.itemChanged(value,item);
        },
        datetimeChanged(value,item){
            value = value && value.unix();
            this.data[item.key]=value;
            this.itemChanged(value,item);
        },
        refClick(item){
            this.docSelectItem = item;
            this.docSelectModal = (item.modal && JSON.parse(item.modal))||undefined;
            this.docSelectModalVisible = true;
        },
        docSelected(selected){
            this.data[this.docSelectItem.key]=selected.docId;
            this.data[this.docSelectItem.key+'$doc']=selected;
        },
        hasError(){
            return this.$refs.form.hasError()
        }
    }
}
</script>

<style scoped>

</style>

<docs>
    - 这是一段文档
</docs>