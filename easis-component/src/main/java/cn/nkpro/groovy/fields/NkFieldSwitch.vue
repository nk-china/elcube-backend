<template>
    <span v-if="!editMode">{{value | formatSwitch(this.inputOptions)}}</span>
    <a-switch size="small" v-else v-model="val" @change="change"></a-switch>
</template>

<script>
export default {
    props:{
        value: {},
        editMode: {
            type: Boolean,
            default:false
        },
        inputOptions: {
            type:Object,
            default(){
                return {}
            }
        }
    },
    filters:{
        formatSwitch(value, options) {
            return value === true ? (options.checked || '是') : (options.unChecked || '否');
        },
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
        change(){
            this.$emit('change',{});
        }
    }
}
</script>

<style scoped>

</style>