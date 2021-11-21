<template>
    <span v-if="!editMode">{{value}}</span>
    <a-input size="small" v-else v-model="val" @change="change" @blur="blur" :max-length="inputOptions.maxLength"></a-input>
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