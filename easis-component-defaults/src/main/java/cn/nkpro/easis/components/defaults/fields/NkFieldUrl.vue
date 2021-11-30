<template>
    <a v-if="!editMode" :href="value" target="_blank">{{value}}</a>
    <a-input size="small" v-else v-model="val" @blur="blur" :max-length="inputOptions.maxLength"></a-input>
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
        blur(){
            this.$emit('change',{});
        }
    }
}
</script>

<style scoped>

</style>