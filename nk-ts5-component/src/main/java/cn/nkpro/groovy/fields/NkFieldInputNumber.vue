<template>
    <span v-if="!editMode">{{value}}</span>
    <a-input-number v-else  v-model="val" @change="change" @blur="blur"></a-input-number>
</template>

<script>
export default {
    props:{
        value: {},
        editMode: Boolean
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