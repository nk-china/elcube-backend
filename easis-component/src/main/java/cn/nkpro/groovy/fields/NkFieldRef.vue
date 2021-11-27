<template>
    <span v-if="!editMode">
        <nk-doc-link :doc="value"></nk-doc-link>
    </span>
    <div v-else>
        <a-input size="small"
                 :read-only="true"
                 @click="docSelectModalVisible=true"
                 style="cursor: pointer; max-width: 300px;"
                 :value="value && value.docName"
        ></a-input>
        <nk-doc-select-modal v-model="docSelectModalVisible"
                             :modal="inputOptions.optionsObject"
                             @select="docSelected"
        ></nk-doc-select-modal>
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
    methods:{
        docSelected(e){
            const value = Object.assign(this.value||{},{
                docId:e.docId,
                docName:e.docName
            });
            this.$emit('input', value);
            this.$emit('change',{});
        }
    }
}
</script>

<style scoped>

</style>