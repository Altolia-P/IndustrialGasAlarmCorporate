# Git Pull 操作指南（个人版）

> 日常拉取更新时的操作流程和注意事项。

---

## 一、Pull 前准备

```bash
# 1. 查看当前分支和本地改动
git status
git stash list

# 2. 暂存未提交的改动（如有）
git stash push -u -m "未完成的工作：xxx"

# 3. 切换到目标分支
git checkout main
# 或 git checkout develop
```

## 二、Pull 操作

```bash
# 标准拉取（推荐 rebase，保持提交历史线性）
git pull --rebase

# 如遇冲突不想处理，可中止 rebase
git rebase --abort
# 改用 merge
git pull --no-rebase
```

## 三、冲突处理

### 常见冲突场景

| 场景 | 处理方式 |
|------|---------|
| 多人改了同一个文件 | 手动保留双方代码 |
| 自己改了但别人删了文件 | 确认后 `git rm` 或恢复 |
| 配置文件冲突 | **手动逐行检查**，不要盲目接受任意一方 |

### 解决流程

```
1. 找到冲突文件（git status 或 IDE 提示）
2. 搜索 <<<<<<<、=======、>>>>>>> 标记
3. 逐段判断保留哪边或合并两边
4. 删除冲突标记
5. git add <file>
6. git rebase --continue 或 git merge --continue
```

### 重要原则

- **任何时候遇到不确定的冲突 → 停下来问人**
- **`.env`、`settings.json`、`application-*.yml` 等配置文件**：以本地为准，不要覆盖
- **大段代码冲突**：用 IDE 的 diff 工具对比后再合并

## 四、Pull 后验证

```bash
# 后端
cd Code/backend/IndustrialGasAlarmCorporate/
mvn clean install -DskipTests    # 确认编译通过

# 前端
cd Code/frontend/
pnpm install                     # 更新依赖
pnpm build                       # 确认构建通过
```

运行项目看一下是否正常启动。

## 五、恢复本地工作

```bash
git stash pop    # 恢复之前暂存的工作
```

如有冲突提示，解决后继续。

## 六、常用回退

```bash
# 拉取出问题，退回到拉取前
git reflog                     # 找到操作前的 commit hash
git reset --hard <hash>

# 放弃所有本地修改（⚠️ 不可逆）
git checkout -- <file>
```

---

## 分支说明

| 分支 | 用途 |
|------|------|
| `main` | 生产就绪代码，稳定 |
| `develop` | 日常开发集成分支 |
| `feature/*` | 功能分支，合并后删除 |
| `fix/*` | 修复分支，合完后删除 |
