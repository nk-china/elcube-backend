<template>
    <span v-if="!editMode">{{value | nkNumber(inputOptions.format||'0.00')}}</span>
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
    computed:{
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
        change(e){
            //this.$emit('change',e);
        },
        blur(e){
            this.$emit('change',e);
        }
    }
}
</script>

<style scoped>

</style>