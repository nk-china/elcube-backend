<template>
    <span v-if="!editMode">

        {{val.value | nkNumber(inputOptions.format||'$ 0,000.00')}} ({{val.percent&&val.percent/100 | nkNumber(inputOptions.format||'0.00%')}})
    </span>
    <a-input-group v-else size="small" compact>
        <a-input-number v-model="val.percent"
                        size="small"
                        @change="change"
                        @blur="blur($event,'percent')"

                        :formatter      ="percentFormat"
                        :parser         ="percentParse"

                        :max="inputOptions.max"
                        :min="inputOptions.min"
                        :precision="inputOptions.digits||4"
                        :step="inputOptions.step"

                        style="width: 120px"
                        placeholder="百分比"
        ></a-input-number>
        <a-input-number  v-model="val.value"
                         size="small"
                         @change="change"
                         @blur="blur($event,'value')"
                         :precision="inputOptions.valueDigits||2"
                         :step="inputOptions.valueStep"

                         style="width: 180px"
                         placeholder="金额"
        ></a-input-number>
    </a-input-group>
</template>

<script>
    export default {
        props:{
            value: {},
            editMode: Boolean,
            inputOptions: {
                type:Object,
                default(){
                    return {}
                }
            }
        },
        data(){
            return {
                changed: undefined,
            }
        },
        computed:{
            val(){
                return this.value||{percent:undefined,value:undefined};
            }
        },
        methods:{
            percentParse(value){
                return value && value.replace(/[,%]/, '');
            },
            percentFormat(value){
                return value && (value+'%');
            },
            change(){
                this.changed = true;
            },
            blur(event,trigger){
                if(this.changed){
                    this.changed = false;
                    const e = {
                        target:trigger
                    };
                    this.$emit('input',this.val);
                    this.$emit('change',e);
                }
            }
        }
    }
</script>

<style scoped>

</style>