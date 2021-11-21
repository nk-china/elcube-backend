<template>
    <span v-if="!editMode">{{value}}</span>
    <a-input-number v-else :defaultValue="value" @change="change"></a-input-number>
</template>

<script>
export default {
    props:{
        value: Number,
        editMode: Boolean
    },
    data(){
        return {
        }
    },
    methods:{
        change(e){
            this.$emit('input',e);
            this.$emit('change',e);
        }
    }
}
</script>

<style scoped>

</style>