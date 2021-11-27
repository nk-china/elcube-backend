<template>
    <span v-if="!editMode">{{value | formatCascader(inputOptions.optionsObject)}}</span>
    <a-cascader size="small"
              v-else
              v-model="val"
              :mode="inputOptions.selectMode||'default'"
              :options="inputOptions.optionsObject"
              @change="change">
    </a-cascader>
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
    filters:{
        formatCascader(value, options){
            if(value) {
                options = typeof options === 'string' ? JSON.parse(options) : options;
                let find = options;
                return value.map(item => {
                    find = find && find.find(o => o.key === item || o.value === item);
                    if (find) {
                        const label = find.label || find.name || find.title;
                        find = find.children;
                        return label;
                    }
                    return item;
                }).join(' / ');
            }
        },
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
        change(){
            this.$emit('change',{});
        }
    }
}
</script>

<style scoped>

</style>