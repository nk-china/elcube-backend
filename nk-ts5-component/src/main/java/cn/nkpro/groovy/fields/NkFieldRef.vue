<template>
    <span v-if="!editMode">{{value}}</span>
    <div v-else>
        <a-input size="small"
                 v-model="val"
                 :read-only="true"
                 @change="change"
                 @blur="blur"
                 @click="docSelectModalVisible=true"
                 style="cursor: pointer;"
        ></a-input>
        <nk-doc-select-modal v-model="docSelectModalVisible" :modal="inputOptions.optionsObject" @select="docSelected"></nk-doc-select-modal>
    </div>
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
    data(){
        return {
            docSelectModalVisible:false,
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
        },
        docSelected(e){
            console.log(e)
        }
    }
}
</script>

<style scoped>

</style>