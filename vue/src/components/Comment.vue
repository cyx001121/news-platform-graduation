<template>
<div>
  <div class="card">
    <h2 style="margin-bottom: 20px">评论 {{commentCount}}</h2>

    <div style="margin-top: 20px">
      <el-input type="textarea" placeholder="请输入评论内容" v-model="commentContent"></el-input>
      <div style="text-align: right;margin-top: 5px">
        <el-button type="primary" @click="addComment">评 论</el-button>
      </div>
    </div>

    <div>
      <!--一级评论 -->
      <div style="display: flex; grid-gap: 20px;margin-bottom: 20px" v-for="item in commentList":key="item.id">
        <!--item 是一个循环中的变量，用于迭代渲染评论列表中的每一项评论信息 -->
        <img :src="item.avatar" alt="" style="width: 50px;height: 50px; border-radius: 50%">
        <div style="flex: 1">
          <div style="color: #666;margin-bottom: 10px">{{item.userName}}</div>
          <div style="color: #444;margin-bottom: 10px">{{item.content}}</div>
          <div style="margin-bottom: 10px">
            <div style="color: #555;font-size: 14px;margin-bottom: 10px" >
              <span style="margin-right: 20px" >{{item.time}}</span>
              <span style="cursor: pointer;color: #777" @click="handleShowReplyInput(item)">
                      <i class="el-icon-s-comment"></i>回 复
                    </span>
              <span v-if="item.userId === user.id" style="cursor: pointer;margin-left: 20px" @click="del(item.id)"><i class="el-icon-delete"></i>删除 </span>
            </div>

            <!--二级评论 -->
            <div v-if="item.showReplyInput">
              <el-input type="textarea" placeholder="请输入回复内容" v-model="item.replyContent"></el-input>
              <div style="text-align: right;margin-top: 5px">
                <el-button type="primary" @click="addReply(item)">回 复</el-button>
              </div>
            </div>


            <!--回复页面 -->
            <div style="display: flex;grid-gap: 20px;margin-bottom: 20px" v-for="sub in item.children" :key="sub.id">
              <img :src="sub.avatar" alt="" style="width: 50px;height: 50px; border-radius: 50%">
              <div style="flex: 1">
                <div style="color: #666;margin-bottom: 10px">{{sub.userName}}
                  <span style="color: #333" v-if="sub.replyUser!==item.userName">回复 {{sub.replyUser}}</span></div>
                <div style="color: #444;margin-bottom: 10px">{{sub.content}}</div>
                <div style="margin-bottom: 10px">
                  <div style="color: #555;font-size: 14px;margin-bottom: 10px" >
                    <span style="margin-right: 20px">{{sub.time}}</span>
                    <span style="cursor: pointer;color: #777" @click="handleShowReplyInput(sub)">
                      <i class="el-icon-s-comment"></i>回 复
                    </span>
                    <span v-if="sub.userId === user.id"style="cursor: pointer;margin-left: 20px" @click="del(sub.id)"><i class="el-icon-delete"></i>删除 </span>
                  </div>
                  <div v-if="sub.showReplyInput">
                    <el-input type="textarea" placeholder="请输入回复内容" v-model="sub.replyContent"></el-input>
                    <div style="text-align: right;margin-top: 5px">
                      <el-button type="primary" @click="addReply(sub)">回 复</el-button>
                    </div>
                  </div>

                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</div>
</template>

<script>
export default {
  name: "CommentComponent",
  props: {
    fid: null,
    module: null
  },
  data() {
    return {
      commentCount: 0,
      commentContent: '',
      commentList: [],
      user: JSON.parse(localStorage.getItem('xm-user') || '{}')
    }
  },
  created() {
    this.loadComment()
  },methods: {
    del(id) {   // 单个删除
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/comment/delete/' + id).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.loadComment()
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    handleShowReplyInput(item) {
      this.$set(item, 'showReplyInput', !item.showReplyInput)
    },
    addReply(item) {
      this.$request.post('/comment/add', { pid: item.id, rootId: item.rootId, content: item.replyContent, fid: this.fid, module: this.module }).then(res => {
        if (res.code === '200') {
          this.$message.success('操作成功')
          item.replyContent = ''
          this.loadComment()  // 重新加载数据
        }
      })
    },
    loadComment() {
      this.$request.get('/comment/selectForUser', {
        params: {  fid: this.fid, module: this.module }
      }).then(res => {
        this.commentList = res.data || []
      })

      this.$request.get('/comment/selectCount', {
        params: { fid: this.fid, module: this.module }
      }).then(res => {
        this.commentCount = res.data || 0
      })
    },
    addComment() {
      this.$request.post('/comment/add', { content: this.commentContent, fid: this.fid, module: this.module }).then(res => {
        if (res.code === '200') {
          this.$message.success('操作成功')
          this.commentContent = ''
          this.loadComment()  // 重新加载数据
        }
      })
    },
  }
}
</script>

<style scoped>

</style>