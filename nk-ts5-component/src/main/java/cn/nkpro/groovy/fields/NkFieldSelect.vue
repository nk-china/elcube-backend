<template>
    <span v-if="!editMode">{{value | nkFromList(inputOptions.optionsJSON)}}</span>
    <a-select v-else
              v-model="val"
              :mode="inputOptions.selectMode||'default'"
              :options="inputOptions.optionsJSON"
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
            this.$emit('change',e);
        }
    }
}
</script>

<style scoped>

</style>