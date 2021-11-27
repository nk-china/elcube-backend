<template>
    <nk-card class="nk-card-doc-snapshots">
        <a-list size="small"  :data-source="joined" style="padding: 0 5px;">
            <a-list-item slot="renderItem" slot-scope="item">
                <a @click="to(item)">V{{ item.version }} | {{item.userRealname}}</a>
                <span slot="extra">
                    {{item.updatedTime | nkDatetimeFriendly}} |
                    <a @click="diff(item)">Diff </a>
                </span>

            </a-list-item>
            <div slot="footer" style="text-align: center" v-if="showMore">
                <a @click="more">More...</a>
            </div>
        </a-list>
    </nk-card>
</template>

<script>
    import Mixin from "Mixin";

    export default {
    mixins:[new Mixin()],
    data(){
        return {
            list:[],
            hasMore:undefined
        }
    },
    computed:{
        joined(){
            const joined = [];
            if(this.data){
                joined.push(...(this.data.slice(0,5)));
            }
            if(this.list.length>1){
                joined.push(...(this.list));
            }
            return joined;
        },
        showMore(){
            return this.hasMore !== undefined ? this.hasMore : (this.data && this.data.length>5);
        }
    },
    methods:{
        to(item){
            this.$router.push(`/apps/docs/snapshot/${item.id}`)
        },
        diff(item){
            this.$router.push(`/apps/docs/diff/${this.doc.docId}/snapshot:${item.id}`)
        },
        more(){
            this.nk$call(this.joined.length)
                .then(data=>{
                    this.list.push(...(data.slice(0,5)));
                    this.hasMore = data.length > 5;
                });
        }
    }
}
</script>

<style scoped lang="less">
    ::v-deep.nk-card-doc-snapshots{
        .ant-list-item{
            font-size:12px;

            a{
                color: rgba(0, 0, 0, 0.65);
            }
            &:hover{
                a{
                    color:#1890ff;
                }
            }
        }
        .ant-list-footer{
            padding: 10px 0 0 0;
            a{
                font-size:10px;
                color:#1890ff;
            }
        }
    }
</style>