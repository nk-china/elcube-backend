<template>
    <span v-if="!editMode">{{value | nkNumber(inputOptions.format||'0.00')}}</span>
    <a-select size="small"
              v-else-if="options"
              v-model="val"
              mode="default"
              :options="options"
              @change="selectChange">
    </a-select>
    <a-input-number size="small"
                    v-else
                    v-model="val"
                    @change="change"
                    @blur="blur"

                    :max="inputOptions.max"
                    :min="inputOptions.min"
                    :precision="inputOptions.digits"
                    :step="inputOptions.step"
    ></a-input-number>
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
            options(){
                if(this.inputOptions && this.inputOptions.options){
                    return this.inputOptions.options.map(i=>{
                        return {key:i,label:i}
                    });
                }
            },
            val:{
                get(){
                    return this.value;
                },
                set(value){
                    this.$emit('input',value);
                }
            }
        },
        methods:{
            change(){
                this.changed = true;
            },
            blur(){
                if(this.changed){
                    this.changed = false
                    this.$emit('change',{});
                }
            },
            selectChange(){
                this.change();
                this.blur();
            }
        }
    }
</script>

<style scoped>

</style>