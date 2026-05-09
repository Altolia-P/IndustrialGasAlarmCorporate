# Git Pull Rules for Claude

> These rules govern how Claude Code should behave when the user performs a `git pull` or when Claude initiates one. This is a system-level instruction file — read and follow on every session.

---

## 1. Pre-Pull Checklist

Before any `git pull` operation, verify:

- [ ] Current branch is correct (`git branch --show-current`)
- [ ] No uncommitted changes that would be lost (`git status --short`)
- [ ] If uncommitted changes exist → **stash them first** with `git stash push -u -m "WIP: <description>"`, and inform the user what was stashed
- [ ] If the user has unstaged work they want to keep → ask before stashing

## 2. Stash Protection Rules

Treat these file types as **protected** during any merge/rebase — never auto-resolve in favor of remote:

| Pattern | Reason |
|---------|--------|
| `.claude/settings.local.json` | Local Claude configuration |
| `.claude/work-log.md` | Personal work log, not in repo |
| `.env*` | Local env secrets |
| `application-dev.yml` / `application-local.yml` | Local DB/Redis credentials |
| `*.local.*` | Any local override file |

**Rule**: If a protected file has a merge conflict, **always keep the local version**. Do not accept remote changes without user confirmation.

## 3. Post-Pull Actions

After a successful pull (merge or rebase completed):

### 3.1 Backend
```bash
# Check if pom.xml changed → dependencies may have changed
cd Code/backend/IndustrialGasAlarmCorporate/
mvn dependency:resolve -q    # quick check without full build
```

### 3.2 Frontend
```bash
# Check if package.json or pnpm-lock.yaml changed → reinstall deps
cd Code/frontend/
# If deps changed:
pnpm install --frozen-lockfile   # or pnpm install if lockfile changed
```

### 3.3 Structural Changes
- If new packages/modules were added (new Maven modules, new frontend routes), notify the user
- If `CLAUDE.md` or `.claude/rules/*` changed → re-read immediately and apply new rules
- If `pom.xml` parent/version changed → run `mvn clean install -DskipTests` to verify

## 4. Conflict Resolution Priority

When resolving merge conflicts automatically (user delegates to me):

| Priority | Rule |
|----------|------|
| 1 (Highest) | Domain layer (`domain/`) files — business invariants must be preserved |
| 2 | Application service files — orchestration logic |
| 3 | Infrastructure — config, repository impl, security |
| 4 | Interface layer — controllers, DTOs |
| 5 (Lowest) | Comments, docs, test files |

**General strategy**: When in doubt, keep **both sides** and flag to user. Never discard code silently.

## 5. Action Table by Scenario

| Scenario | Claude Action |
|----------|---------------|
| User says "pull the latest" | Run `git pull --rebase` after checking/stashing. If conflict → pause and show user |
| Pull introduces new DB migration files | Flag to user: "New migration found, run manually if needed" |
| Pull updates CLAUDE.md | Re-apply all rules from updated CLAUDE.md |
| Pull introduces new dependencies | Run install command for the affected project |
| Pull after user force-pushed | Check `git log --oneline HEAD@{1}..HEAD` for unexpected history changes |
| Pull results in detached HEAD | Notify user and suggest `git checkout <branch>` |

## 6. What to Log in work-log.md

After a pull operation, append a row:

```markdown
| HH:mm | git pull — <summary of what changed> | ✅ |
```

Include: new features pulled, config changes, or dependency updates. Skip trivial commits.

## 7. Hard Rules (Do Not Violate)

1. **Never** run `git reset --hard` or `git checkout -- .` without explicit user approval
2. **Never** overwrite `.claude/` directory contents from remote without user confirmation
3. **Never** auto-merge `application-*.yml` or `.env` files — always defer to local
4. **Never** delete the user's stashed work without asking
