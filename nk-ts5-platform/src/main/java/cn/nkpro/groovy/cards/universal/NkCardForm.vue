<template>
    <nk-card>
        <nk-form ref="form" :col="def.col" :edit="editMode" style="width: 80%">
            <template v-for="(item,index) in def.items" >
                <nk-form-divider
                        v-if="item.inputType==='divider'"
                        :key="index"
                        :term="item.name"></nk-form-divider>
                <nk-form-item
                        v-else
                        :edit="!item.readonly"
                        :key="item.key"
                        :term="item.name"
                        :col="item.col"
                        :validateFor="item.fieldValue"
                        :required="!!item.required"
                        :len="item.options&&item.options.len"
                        :min="item.options&&item.options.min"
                        :max="item.options&&item.options.max"
                        :pattern="item.options&&item.options.pattern"
                        :message="item.options&&item.options.message"
                >
                    {{data[item.key]}}
                    <a-input        v-if            ="item.inputType==='text'"
                                    slot            ="edit"
                                    size            ="small"
                                    style           ="max-width: 300px;"
                                    v-model         ="data[item.key]"
                                    :maxLength      ="item.maxLength"
                                    @blur           ="itemChanged($event,item)"/>
                    <a-input-number v-else-if       ="item.inputType==='integer'"
                                    slot            ="edit"
                                    size            ="small"
                                    style           ="max-width: 300px;"
                                    :style          ="item.options&&item.options.style"
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
                                    :style          ="item.options&&item.options.style"
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
                                    :style          ="item.options&&item.options.style"
                                    v-model         ="data[item.key]"
                                    :min            ="item.min"
                                    :max            ="item.max"
                                    :precision      ="item.digits||2"
                                    :step           ="item.step||0.01"
                                    @blur           ="itemChanged($event,item)"
                                    :formatter      ="percentFormat($event,item)"
                                    :parser         ="percentParse($event,item)"
                    />
                    <a-date-picker  v-else-if       ="item.inputType==='date'"
                                    slot            ="edit"
                                    size            ="small"
                                    :style          ="item.options&&item.options.style"
                                    v-model         ="data[item.key]"
                                    @change         ="dateChanged($event,item)"></a-date-picker>
                    <a-date-picker  v-else-if       ="item.inputType==='datetime'"
                                    show-time
                                    slot            ="edit"
                                    size            ="small"
                                    :style          ="item.options&&item.options.style"
                                    v-model         ="data[item.key]"
                                    @change         ="datetimeChanged($event,item)"></a-date-picker>
                    <a-select       v-else-if       ="item.inputType==='select'"
                                    slot            ="edit"
                                    v-model         ="data[item.key]"
                                    size            ="small"
                                    style           ="max-width: 250px;"
                                    :style          ="item.options&&item.options.style"
                                    @change         ="selectChanged($event,item)"
                                    :options        ="JSON.parse(item.options)">
                    </a-select>
                    <a-select       v-else-if       ="item.inputType==='multiple'"
                                    slot            ="edit"
                                    v-model         ="data[item.key]"
                                    mode            ="multiple"
                                    size            ="small"
                                    style           ="max-width: 250px;"
                                    :style          ="item.options&&item.options.style"
                                    @change         ="multipleChanged($event,item)"
                                    :options        ="JSON.parse(item.options)">
                    </a-select>
                    <a-cascader     v-else-if       ="item.inputType==='cascader'"
                                    slot            ="edit"
                                    size            ="small"
                                    v-model         ="data[item.key]"
                                    style           ="max-width: 250px;"
                                    :options        ="JSON.parse(item.options)">
                    </a-cascader>
                    <a-tree-select  v-else-if       ="item.inputType==='tree'"
                                    slot            ="edit"
                                    v-model         ="data[item.key]"
                                    style           ="max-width: 350px;"
                                    :style          ="item.options&&item.options.style"
                                    size            ="small"
                                    :tree-data      ="JSON.parse(item.options)"
                                    tree-checkable
                                    :show-checked-strategy="SHOW_PARENT"
                                    search-placeholder="Please select"
                                    @change="treeChanged($event,field)"
                    />
                    <label          v-else-if       ="item.inputType==='ref'"
                                    slot            ="edit"
                                    class           ="ref-input ant-input-affix-wrapper"
                                    :style          ="item.options&&item.options.style"
                    >
                        <input      class="ant-input ant-input-sm"
                                    v-model="item.fieldDisplay"
                                    readonly
                                    @click="refClick(field)" />
                        <span       class="ant-input-suffix">
                            <a-icon type="select" style="color: rgba(0,0,0,.45)" />
                        </span>
                    </label>
                </nk-form-item>
            </template>
        </nk-form>
    </nk-card>
</template>

<script>
import { Mixin } from "nk-ts5-platform";
import numeral from "numeral";
import moment from "moment";
import { Interpreter } from "eval5";
import { TreeSelect } from 'ant-design-vue';

export default {
    mixins:[new Mixin()],
    filters:{
        dataValueFilter(value){
            return value?moment(value*1000):null;
        }
    },
    created() {
        console.log(this.def.items)
    },
    data(){
        return {
            SHOW_PARENT:TreeSelect.SHOW_PARENT
        }
    },
    computed:{
        list(){
            return this.data;
        }
    },
    methods:{
        percentParse(value){
            return value && numeral(value).format(item.format||'#.00%');
        },
        percentFormat(value,item){
            return value && value.replace(/[,%]/, '')/100;
        },
        itemChanged(value,item){
            if(item.calcTrigger){
                this.nk$calc();
            }
        },
        dateChanged(value,item){
            this.itemChanged((value && (value.hour(0).minute(0).second(0).millisecond(0).valueOf()/1000))||'',item);
        },
        datetimeChanged(value,item){
            this.itemChanged((value && (value                                           .valueOf()/1000))||'',item);
        },
        selectChanged(value,item){
            // field.fieldValue=value;
            // if(field.options.options){
            //     field.fieldDisplay=field.options.options.find(e=>e.value===value).label;
            // }else{
            //     field.fieldDisplay=field.fieldValue;
            // }
            // if(field.inputCalc===1){
            //     this.$nkCalc("options");
            // }
        },
        multipleChanged(){

        },
        treeChanged(){

        },
        refClick(){

        }
    }
}
</script>

<style scoped>

</style>

<docs>
    - 这是一段文档
</docs>