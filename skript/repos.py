#!/usr/bin/env -S uv run --script
# /// script
# requires-python = ">=3.13"
# dependencies = []
# ///

import json
import pathlib
import subprocess
from dataclasses import dataclass
from enum import StrEnum, auto

GITHUB_TEAM = "teamforeldrepenger"
GITHUB_ORG = "navikt"
GITHUB_SSH_URL = "git@github.com"
GITHUB_URL_BASE = f"{GITHUB_SSH_URL}:{GITHUB_ORG}/"


REPO_DIR = pathlib.Path.cwd() / "repositories"
REPOSITORY_FILEPATH = REPO_DIR / "repos.txt"


class RepoType(StrEnum):
    BACKEND = "backend"
    FRONTEND = "frontend"
    BIBLIOTEK = "bibliotek"
    JAVA = "java"
    TYPESCRIPT = "typescript"


REPO_PATH = {
    RepoType.BACKEND: REPO_DIR / "backend.txt",
    RepoType.FRONTEND: REPO_DIR / "frontend.txt",
    RepoType.BIBLIOTEK: REPO_DIR / "bibliotek.txt",
    RepoType.JAVA: REPO_DIR / "java.txt",
    RepoType.TYPESCRIPT: REPO_DIR / "typescript.txt",
}


class RoleName(StrEnum):
    ADMIN = auto()
    MAINTAIN = auto()
    WRITE = auto()
    READ = auto()


@dataclass(frozen=True)
class Repository:
    name: str
    full_name: str
    role_name: RoleName
    topics: list[str]
    archived: bool
    language: str

    def is_active(self) -> bool:
        return self.is_admin() and not self.is_archived()

    def is_archived(self) -> bool:
        return self.archived

    def belongs_to(self, subteam: str) -> bool:
        return (
            any([subteam == topic for topic in self.topics]) if self.topics else False
        )

    def module_owners(self) -> list[str]:
        return sorted([topic for topic in self.topics if "-modul-" in topic])

    def is_admin(self) -> bool:
        return self.role_name == RoleName.ADMIN or self.role_name == RoleName.MAINTAIN

    def is_java(self) -> bool:
        return self.language == "Java"

    def is_typescript(self) -> bool:
            return self.language == "TypeScript"

    def __hash__(self):
        return hash(self.name)

    def __gt__(self, other) -> bool:
        return self.name > other.name


def get_repositories(github_team: str) -> list[Repository]:
    gh_command = f"gh api /teams/{_get_github_team_id(github_team)}/repos --paginate"
    output = subprocess.check_output(gh_command, shell=True)
    data = json.loads(output)
    if isinstance(data, list):
        return [
            Repository(
                name=repo["name"],
                full_name=repo["full_name"],
                role_name=RoleName(repo["role_name"]),
                topics=list(set(repo["topics"])),
                archived=repo["archived"],
                language=repo["language"],
            )
            for repo in data
        ]
    else:
        raise TypeError(f"Expected list, got {type(data)}")


def _get_github_team_id(github_team: str) -> str:
    gh_command = f"gh api /orgs/navikt/teams/{github_team}"
    output = subprocess.check_output(gh_command, shell=True)
    data = json.loads(output)
    return data.get("id")


def write_repolist(repos: list[Repository], path: pathlib.Path):
    with open(path, "w") as f:
        f.write("\n".join([repo.full_name for repo in sorted(repos)]) + "\n")
    print(f"Laget liste av repoer i: {path}")


def filter_active_repos(repos: list[Repository]) -> list[Repository]:
    return [repo for repo in repos if repo.is_active()]


def print_repo_topics(active_repos):
    print("\nRepoer med tags:")
    for repo in sorted(active_repos):
        if repo.topics:
            print(f"{repo.name}:\n{'\n'.join(repo.topics)}")
        else:
            print(f"{repo.name}: No topics")


def count_repo_topics(active_repos):
    topic_counts = {}
    for repo in active_repos:
        for topic in repo.topics:
            if topic not in topic_counts:
                topic_counts[topic] = 0
            topic_counts[topic] += 1
    print("Antall repoer per tag:")
    for topic, count in sorted(topic_counts.items(), key=lambda x: x[1], reverse=True):
        print(f"{topic}: {count}")
    print()


def fordel_repoer(repos: list[Repository]) -> dict[RepoType, list[Repository]]:
    backend_repos = sorted(
        [
            repo
            for repo in repos
            if repo.belongs_to("backend")
            and not repo.belongs_to("frontend")
            and not repo.belongs_to("bibliotek")
        ],
        key=lambda r: r.name,
    )

    frontend_repos = sorted(
        [
            repo
            for repo in repos
            if repo.belongs_to("frontend")
            and not repo.belongs_to("backend")
            and not repo.belongs_to("bibliotek")
        ],
        key=lambda r: r.name,
    )

    bibliotek_repos = sorted(
        [
            repo
            for repo in repos
            if repo.belongs_to("bibliotek")
        ],
        key=lambda r: r.name,
    )

    other_repos = sorted(
        [
            repo
            for repo in repos
            if not (
                repo.belongs_to("frontend")
                or repo.belongs_to("backend")
                or repo.belongs_to("bibliotek")
            )
        ],
        key=lambda r: r.name,
    )

    java_repos = sorted(
        [
            repo
            for repo in repos
            if repo.is_java()
        ],
        key=lambda r: r.name,
    )

    typescript_repos = sorted(
        [
            repo
            for repo in repos
            if repo.is_typescript()
        ],
        key=lambda r: r.name,
    )

    other_language_repos = sorted(
        [
            repo
            for repo in repos
            if not (
                repo.is_java()
                or repo.is_typescript()
            )
        ],
        key=lambda r: r.name,
    )

    antall_fordelte_repoer = (
        len(backend_repos)
        + len(frontend_repos)
        + len(bibliotek_repos)
        + len(other_repos)
    )

    antall_repoer = len(repos)
    print(f"Antall repoer totalt: {antall_repoer}")
    print(f"Antall fordelte repoer: {antall_fordelte_repoer}")
    print(f"Antall Java repoer: {len(java_repos)}")
    print(f"Antall TypeScript repoer: {len(typescript_repos)}")
    print(f"Antall other language repoer: {len(other_language_repos)}")

    return {
        RepoType.BACKEND: backend_repos,
        RepoType.FRONTEND: frontend_repos,
        RepoType.BIBLIOTEK: bibliotek_repos,
        RepoType.JAVA: java_repos,
        RepoType.TYPESCRIPT: typescript_repos,
    }


def print_repos_with_module_owners(repos: list[Repository]):
    for repo in repos:
        print(f"- {repo.name}")
        owners = repo.module_owners()
        if owners:
            for owner in owners:
                print(f"  - {owner}")
    print()


def main():
    repos = get_repositories(github_team=GITHUB_TEAM)
    active_repos = filter_active_repos(repos)
    count_repo_topics(active_repos)
    repoer = fordel_repoer(active_repos)

    write_repolist(active_repos, REPOSITORY_FILEPATH)

    for team, repolist in repoer.items():
        write_repolist(repolist, REPO_PATH[team])

    print("\nBackend-repoer:")
    print_repos_with_module_owners(repoer[RepoType.BACKEND])
    print("\nFrontend-repoer:")
    print_repos_with_module_owners(repoer[RepoType.FRONTEND])
    print("\nBibliotek-repoer:")
    print_repos_with_module_owners(repoer[RepoType.BIBLIOTEK])
    print("\nJava-repoer:")
    print_repos_with_module_owners(repoer[RepoType.JAVA])
    print("\nTypeScript-repoer:")
    print_repos_with_module_owners(repoer[RepoType.TYPESCRIPT])


if __name__ == "__main__":
    main()
