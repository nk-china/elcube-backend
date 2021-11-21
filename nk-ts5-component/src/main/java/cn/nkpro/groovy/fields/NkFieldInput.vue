<template>
    <span v-if="!editMode">{{value}}</span>
    <a-input v-else :defaultValue="value" v-model="val" @change="change"></a-input>
</template>

<script>
export default {
    props:{
        value: {},
        editMode: Boolean
    },
    data(){
        return {
            val:undefined
        }
    },
    methods:{
        change(e){
            this.$emit('input',this.val);
            this.$emit('change',e);
        }
    }
}
</script>

<style scoped>

</style>