<template>
    <span v-if="!editMode">{{value | nkDatetime}}</span>
    <a-date-picker size="small" v-else v-model="val" @change="change"></a-date-picker>
</template>

<script>
    import moment from 'moment';
    export default {
        props:{
            value: Number,
            editMode: Boolean
        },
        computed:{
            val:{
                get(){
                    return this.value?moment(this.value*1000):undefined;
                },
                set(value){
                    value = value && value.startOf('day').unix();
                    this.$emit('input',value);
                }
            }
        },
        methods:{
            moment,
            change(value){
                this.$emit('change',value);
            }
        }
    }
</script>

<style scoped>

</style>