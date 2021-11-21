<template>
    <span v-if="!editMode">{{value}}</span>
    <a-select v-else
              :defaultValue="value"
              :options="inputOptions.options"
              @change="change">
    </a-select>
</template>

<script>
export default {
    props:{
        editMode: Boolean,
        value: {},
        inputOptions: {
            type:Object,
            default(){
                return {}
            }
        }
    },
    data(){
        return {}
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