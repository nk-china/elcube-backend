<template>
    <span v-if="!editMode">
        {{display | nkDatetimeISO(inputOptions.format||(inputOptions.showTime?'YYYY/MM/DD HH:mm:ss':'YYYY/MM/DD'))}}
    </span>
    <a-date-picker size="small" v-else v-model="val" @change="change" :show-time="inputOptions.showTime"></a-date-picker>
</template>

<script>
    import moment from 'moment';
    import NkFormat from 'NkFormat';
    export default {
        props:{
            value: {},
            editMode: Boolean,
            inputOptions: {
                type:Object,
                default(){
                    return {}
                }
            }
        },
        computed:{
            display(){
                if(this.value){
                    let val = this.value;
                    if(typeof val !== 'number'){
                        val = moment(this.value, this.inputOptions.formatStorage).valueOf();
                    }else if(!this.inputOptions.formatStorage){
                        val = val * 1000;
                    }
                    return val;
                }
            },
            val:{
                get(){
                    if(this.value){
                        if(this.inputOptions.formatStorage){
                            if(typeof this.value === 'number'){
                                const v = moment(this.value);
                                this.$emit('input',v.format(this.inputOptions.formatStorage));
                                return v;
                            }
                            return moment(this.value, this.inputOptions.formatStorage)
                        }else{
                            return moment(this.value * 1000)
                        }
                    }
                    return undefined;
                },
                set(value){

                    if(this.inputOptions.formatStorage){
                        value = value && value.format(this.inputOptions.formatStorage);
                        this.$emit('input',value);
                    }else{
                        if(this.inputOptions.showTime){
                            value = value && value.unix();
                            this.$emit('input',value);
                        }else{
                            value = value && value.startOf('day').unix();
                            this.$emit('input',value);
                        }
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