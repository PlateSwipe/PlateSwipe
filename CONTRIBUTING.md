# Contributing to PlateSwipe

Thank you for your interest in contributing to the PlateSwipe project! This document outlines the process and guidelines for contributing code, making pull requests, and maintaining code quality. Please follow these steps to ensure smooth collaboration and code integration.

## Table of Contents
1. [Branching Strategy](#branching-strategy)
2. [Pull Request Process](#pull-request-process)
3. [Code Style and Linters](#code-style-and-linters)
4. [Commit Messages](#commit-messages)

---

## Branching Strategy

We follow a structured branching strategy to keep the codebase organized. Please adhere to the following branch naming conventions:

### Main Branches:
- **`main`**: This is the production-ready branch. All stable code goes here.
- **`develop`**: This is the integration branch where features and fixes are merged after testing.

### Feature and Fix Branches:
- **`feature/feature-name`**: For developing new features.
  - Example: `feature/user-authentication`
- **`bugfix/issue-number-description`**: For resolving bugs or issues.
  - Example: `bugfix/123-fix-login-error`
- **`chore/task-name`**: For maintenance or non-feature tasks.
  - Example: `chore/ci-setup`
- **`hotfix/description`**: For emergency fixes in production.
  - Example: `hotfix/critical-security-patch`

Make sure to regularly update your branch with the latest changes from `develop` to avoid merge conflicts. Always create new branches from `develop` unless instructed otherwise.

---

## Pull Request Process

All contributions must go through the pull request (PR) process to ensure code quality and prevent issues in the codebase. Follow these steps when submitting a pull request:

### 1. **Create a Branch**:
   - Create your branch following the [branch naming conventions](#branching-strategy).
   - Make sure your branch is up-to-date with the `develop` branch before starting your work.
   
### 2. **Write Descriptive PRs**:
   - **Title**: Give your PR a concise and descriptive title (e.g., "Add user authentication").
   - **Description**: In the description, mention:
     - What has changed.
     - Why the changes were necessary.
     - Any special considerations or dependencies.
   - **Link to Issues**: If applicable, link to the related issue (e.g., "Resolves #123").
   
### 3. **Run CI/CD Pipelines**:
   - Ensure that all CI checks pass (such as tests, linters, and code format checks) before submitting the PR.
   - If you encounter failing tests, resolve them before requesting a review.
   
### 4. **Review Process**:
   - Submit your PR for review.
   - At least one team member should approve the PR before merging. For significant changes, two approvals may be required.
   - Be open to feedback and make requested changes promptly.

### 5. **Merge the PR**:
   - After receiving the necessary approvals and passing all CI checks, you can merge the PR into `develop` (or `main` for hotfixes).
   - Delete the feature or fix branch after the merge.

---

## Code Style and Linters

We maintain consistent code quality across the project using style guides and automated linting. Please follow these standards:

### **Kotlin/Java Code Style**:
   - Use **KTFmt** for Kotlin formatting and ensure that your code passes the formatting checks.
     - You can run the formatter locally using:
       ```bash
       ./gradlew ktfmtCheck
       ```
   - Ensure your code is clean and adheres to best practices in Kotlin/Java.
   
### **XML Formatting (for layouts and configurations)**:
   - Follow the standard XML formatting rules provided by Android Studio. Ensure proper indentation and spacing.
   
### **Other Tools**:
   - **SonarQube**: We use SonarQube for static code analysis. Ensure your code is free from critical issues or major code smells.

---

## Commit Messages

Writing clear and descriptive commit messages is crucial for maintaining a clean commit history. We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification to structure commit messages.

### **Commit Message Format**:
Each commit message should consist of three parts: a **type**, an **optional scope**, and a **description**. Here’s the format:

```
<type>(<scope>): <description>
```

### **Commit Types**:
- **`feat`**: Introduces a new feature.
  - Example: `feat(auth): add user authentication`
- **`fix`**: Fixes a bug.
  - Example: `fix(login): resolve null pointer exception`
- **`chore`**: Non-feature tasks such as updating dependencies or configuring CI.
  - Example: `chore(deps): update Gradle dependencies`
- **`refactor`**: Code changes that don’t fix bugs or add features.
  - Example: `refactor(todo): improve list rendering performance`
- **`docs`**: Changes to documentation.
  - Example: `docs(readme): update contributing guidelines`
- **`test`**: Adding or updating tests.
  - Example: `test(api): add tests for authentication endpoints`

### **Best Practices for Commit Messages**:
- Use the **imperative mood** (e.g., "fix", "add", "refactor").
- **Capitalize the first letter** of the subject.
- Keep the **subject line under 50 characters**.
- Use the **body of the commit** for more detailed explanations if necessary (wrap text at 72 characters).
- Reference issues or PRs where relevant.

---

## Conclusion

By following these guidelines, we can ensure that the codebase remains clean, maintainable, and easy to collaborate on. Thank you for taking the time to contribute to the PlateSwipe project. We appreciate your efforts in improving the code and making this a better project for everyone!

If you have any questions or need clarification, feel free to reach out via GitHub Issues or Discussions.
