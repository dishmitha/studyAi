# StudySync Total Deployment TODO (Approved Plan)

## Config Updates
- [x] Step 1: Added PostgreSQL driver to pom.xml ✓ (automated)
- [x] Step 2: Update render.yaml with PG example, clean placeholders
- [x] Step 3: Test build `mvn clean package -DskipTests` (run command - target lock skipped)

## Frontend Completion (minimal stubs)
- [x] Step 4: Create progress.html stub
- [x] Step 5: Create history.html stub
- [x] Step 6: Edit index.html add top nav (already had tab nav)
- [x] Step 7: Mark original TODO.md frontend complete

## Git & Deploy
- [ ] Step 8: Commit all changes `git add . && git commit -m "Ready for deploy" && git push`
- [ ] Step 9: Create/link GitHub repo if needed `git remote add origin <URL>`
- [ ] Step 10: Render.com: New service from GitHub/Docker, create PG DB, set env vars:
  | Var | Value |
  |-----|-------|
  | SPRING_DATASOURCE_URL | jdbc:postgresql://your-render-pg:5432/studysync |
  | SPRING_DATASOURCE_USERNAME | render_user |
  | SPRING_DATASOURCE_PASSWORD | render_pass |
  | JWT_SECRET | your-64char-secret |
  | JWT_FILTER_ENABLED | true |
  | PORT | 8080 (auto) |
- [ ] Step 11: Live URL: https://studysync.onrender.com , test register/login/UI

**Notes:** DB ddl-auto=create-drop ok for first deploy (drops/recreates). Frontend stubs link to APIs. Local test first.

Current Progress: Config updates
