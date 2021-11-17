<template>
    <nk-card>
        <nk-form :col="1" :edit="editMode">
            <nk-form-item title="姓名" v-if="fields.name.visible">
                {{data.name}}
                <a-input-search v-if="!fields.name.readonly"
                                v-model="data.name"
                                slot="edit"
                                size="small"
                                style="width: 200px;"
                                enter-button="获取"
                                @search="search"
                ></a-input-search>
            </nk-form-item>
            <nk-form-item title="年龄" v-if="fields.age.visible">
                {{data.age}}
                <a-input-number v-if="!fields.age.readonly" v-model="data.age" slot="edit" size="small" :min="0" :max="120" style="width: 200px;"/>
            </nk-form-item>
            <nk-form-item title="性别" v-if="fields.sex.visible">
                {{data.sex | nkFromList(fields.sex.options)}}
                <a-select v-if="!fields.sex.readonly" v-model="data.sex" :options="fields.sex.options" slot="edit" size="small" style="width: 200px;"/>
            </nk-form-item>
            <nk-form-item title="爱好" v-if="fields.likes.visible">
                {{data.likes}}
                <a-input v-if="!fields.likes.readonly" v-model="data.likes" slot="edit" size="small" style="width: 200px;"/>
            </nk-form-item>
        </nk-form>
    </nk-card>
</template>

<script>
    import Mixin from "Mixin";

    export default {
        mixins:[new Mixin()],
        computed:{
            fields(){
                let fields = {};
                this.def.fields && this.def.fields.forEach(field=>{
                    fields[field.key]=field;
                });
                return fields;
            }
        },
        methods:{
            search(){
                this.nk$calc("search");
            }
        }
    }
</script>

<style scoped>

</style>

<docs>
    - 这是一段文档
</docs>