<template>
    <span v-if="!editMode">
        {{value | nkDatetime(inputOptions.format||(inputOptions.showTime?'YYYY/MM/DD HH:mm:ss':'YYYY/MM/DD'))}}
    </span>
    <a-date-picker size="small" v-else v-model="val" @change="change" :show-time="inputOptions.showTime"></a-date-picker>
</template>

<script>
    import moment from 'moment';
    export default {
        props:{
            value: Number,
            editMode: Boolean,
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
                    return this.value?moment(this.value*1000):undefined;
                },
                set(value){
                    if(this.inputOptions.showTime){
                        value = value && value.unix();
                        this.$emit('input',value);
                    }else{
                        value = value && value.startOf('day').unix();
                        this.$emit('input',value);
                    }
                }
            }
        },
        methods:{
            moment,
            change(){
                this.$emit('change',{});
            }
        }
    }
</script>

<style scoped>

</style>