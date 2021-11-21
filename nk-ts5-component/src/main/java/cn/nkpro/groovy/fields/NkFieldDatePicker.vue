<template>
    <span v-if="!editMode">{{value | nkDatetime}}</span>
    <a-date-picker v-else :defaultValue="value?moment(value*1000):undefined" @change="change"></a-date-picker>
</template>

<script>
    import moment from 'moment';
    export default {
        props:{
            value: Number,
            editMode: Boolean
        },
        methods:{
            moment,
            change(value){
                value = value && value.startOf('day').unix();
                this.$emit('input',value);
            }
        }
    }
</script>

<style scoped>

</style>