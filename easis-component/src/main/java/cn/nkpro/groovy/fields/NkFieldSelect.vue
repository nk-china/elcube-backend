<template>
    <span v-if="!editMode">
        <template v-if="inputOptions.tagColor">
            <a-tag v-for="(item,index) in list" :key="index" :color="inputOptions.tagColor">{{item | nkFromList(inputOptions.optionsObject)}}</a-tag>
        </template>
        <template v-else>
            {{value | nkFromList(inputOptions.optionsObject)}}
        </template>
    </span>
    <a-select size="small"
              v-else
              v-model="val"
              :mode="inputOptions.selectMode||'default'"
              :options="inputOptions.optionsObject"
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
        list(){
            if(this.value){
                return typeof this.value === 'object' && this.value[0] ? this.value : [this.value];
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
            this.$emit('change',{});
        }
    }
}
</script>

<style scoped>

</style>