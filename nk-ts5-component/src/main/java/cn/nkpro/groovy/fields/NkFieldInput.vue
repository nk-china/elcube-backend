<template>
    <div>
        <a-input v-if="editMode" v-model="val" @change="change" @blur="blur"></a-input>
        <span v-else>{{value}}</span>
    </div>
</template>

<script>
export default {
    props:{
        value: {},
        editMode: {
            type: Boolean,
            default:true
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