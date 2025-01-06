# Services de Gestion de Base de Données Oracle : Aperçu Général

Ce document présente une suite de services Java conçus pour simplifier la gestion et la surveillance d'une base de données Oracle. Ces services offrent des fonctionnalités complètes pour les opérations de sauvegarde et de restauration, la sécurité des données, le contrôle des utilisateurs et des accès, l'administration des tables, l'optimisation des performances et la surveillance en temps réel.

## Description des Services

Les services suivants sont inclus :

### 1. `BackupService` : Sauvegarde et Restauration de la Base de Données

Ce service est dédié à la gestion complète du cycle de vie des sauvegardes de votre base de données Oracle.

**Fonctionnalités Clés :**

*   **`triggerBackup(String type)` :** Lance une sauvegarde, qu'elle soit complète (`COMPLETE`) ou incrémentielle (`INCREMENTAL`), en utilisant des scripts RMAN. Le statut de la sauvegarde (réussite ou échec) est enregistré.
*   **`getBackupHistory()` :** Fournit l'historique complet des opérations de sauvegarde.
*   **`getBackupsByDateRange(LocalDateTime start, LocalDateTime end)` :** Filtre l'historique des sauvegardes par plage de dates.
*   **`restoreBackup(Long id)` :** Effectue la restauration de la base de données à partir d'une sauvegarde spécifique identifiée par son ID. Le statut de la restauration est également suivi.
*   **`scheduledBackup()` :** Planifie des sauvegardes automatisées à l'aide d'une expression cron.

**Paramètres de Configuration Importants :**

*   `oracle.rman.command.complete` : Définit la commande RMAN pour les sauvegardes complètes.
*   `oracle.rman.command.incremental` : Définit la commande RMAN pour les sauvegardes incrémentielles.
*   `backup.schedule.cron` : Spécifie l'expression cron pour les sauvegardes planifiées.
*   `restore.timeout` : Définit la période de délai d'attente pour les opérations de restauration.

### 2. `DatabaseSecurityService` : Sécurité des Données Améliorée

Ce service offre des fonctionnalités pour sécuriser votre base de données Oracle en utilisant plusieurs techniques :

**Fonctionnalités Clés :**

*   **`enableTDE(String tableName, String columnName)` :** Active le Transparent Data Encryption (TDE) pour une colonne de table spécifique, en utilisant le chiffrement AES256. La politique de chiffrement est enregistrée.
*   **`enableAudit(String tableName)` :** Active l'audit pour les opérations DML sur une table spécifiée.
*   **`configureVPD(String tableName, String policyFunction)` :** Configure les politiques de Virtual Private Database (VPD) pour une table en utilisant la fonction de politique fournie.

### 3. `RmanServiceImpl` : Interaction Directe avec RMAN

Ce service propose une approche alternative pour interagir avec le RMAN d'Oracle en utilisant `docker exec` pour exécuter des opérations à l'intérieur du conteneur de la base de données.

**Fonctionnalités Clés :**

*   **`performFullBackup()` :** Exécute une sauvegarde complète de la base de données à l'aide de RMAN.
*   **`performIncrementalBackup(int level)` :** Exécute une sauvegarde incrémentielle de la base de données en fonction du niveau spécifié.
*   **`listBackups()` :** Récupère tous les enregistrements de l'historique des sauvegardes.
*   **`performRestore()` :** Restaure la base de données à partir d'une sauvegarde et l'ouvre à l'aide de RMAN et SQLPlus.
*   **`getBackupHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate)` :** Récupère les enregistrements de l'historique des sauvegardes dans une plage de dates donnée.

### 4. `RoleService` : Gestion des Rôles Utilisateur

Ce service fournit des fonctionnalités complètes de gestion des rôles utilisateurs.

**Fonctionnalités Clés :**

*   **`assignRoleToUser(String username, String role)` :** Attribue un rôle spécifié à un utilisateur. Gère les rôles personnalisés en ajoutant le préfixe `c##`.
*   **`createRole(String roleName)` :** Crée un nouveau rôle personnalisé.
*   **`grantPrivilegesToRole(String roleName, String[] privileges)` :** Accorde les privilèges spécifiés à un rôle.
*   **`createRoleWithPrivileges(String roleName, String[] privileges)` :** Crée un nouveau rôle avec les privilèges spécifiés.
*   **`revokeRoleFromUser(String username, String privilege)` :** Révoque un privilège d'un utilisateur.
*   **`dropRole(String roleName)` :** Supprime un rôle.
*   **`getAllRoles()` :** Récupère tous les rôles existants.
*   **`getUserPrivileges(String username)` :** Obtient les privilèges attribués à un utilisateur.
*   **`getRolePrivileges(String roleName)` :** Obtient les privilèges attribués à un rôle.
*   **`getUserRoles(String username)` :** Obtient les rôles attribués à un utilisateur.

### 5. `TableManagementService` : Administration des Tables

Ce service offre la possibilité de gérer les tables de la base de données.

**Fonctionnalités Clés :**

*   **`createTable(String tableName, List<String> columns)` :** Crée une nouvelle table avec les colonnes spécifiées. Inclut une colonne de clé primaire `id`, des déclencheurs et des séquences pour l'auto-incrémentation.
*   **`listTables()` :** Récupère toutes les métadonnées des tables.
*   **`getTableInfo(String tableName)` :** Récupère les métadonnées détaillées d'une table spécifique.
*   **`editTable(String tableName, List<String> newColumns)` :** Modifie une table existante en ajoutant ou en supprimant des colonnes.
*   **`deleteTable(String tableName)` :** Supprime une table, y compris son déclencheur et sa séquence associés.
*   **`listTableNames()` :** Récupère les noms de toutes les tables.
*   **`listTable()` :** Récupère tous les objets de métadonnées des tables.
*   **`listColumns(String tableName)` :** Récupère les noms des colonnes d'une table.

### 6. `UserService` : Gestion des Comptes Utilisateur

Ce service fournit des fonctionnalités de gestion des comptes utilisateurs.

**Fonctionnalités Clés :**

*   **`createUser(User user)` :** Crée un nouveau compte d'utilisateur de base de données, en créant également l'utilisateur dans la base de données Oracle.
*   **`updateUser(Long id, User updatedUser)` :** Met à jour un compte d'utilisateur existant, y compris les propriétés de l'utilisateur Oracle telles que le mot de passe et le tablespace.
*   **`getAllUsers()` :** Récupère tous les comptes utilisateurs existants.
*   **`getUserById(Long id)` :** Récupère un compte utilisateur spécifique par son ID.
*   **`deleteUser(Long id)` :** Supprime un compte utilisateur, en supprimant également l'utilisateur dans la base de données Oracle.

### 7. `PerformanceOptimizationService` : Optimisation de la Base de Données

Ce service est axé sur l'optimisation des performances de votre base de données Oracle.

**Fonctionnalités Clés :**

*   **`identifySlowQueries()` :** Identifie les requêtes SQL lentes.
*   **`optimizeQuery(Long queryId)` :** Fournit des recommandations d'optimisation pour une requête lente spécifique en utilisant `DBMS_SQLTUNE`.
*   **`scheduleStatisticsGathering(StatisticsJob job)` :** Planifie des tâches de collecte de statistiques pour les tables et les index.
*   **`executeScheduledStatisticsJobs()` :** Exécute les tâches planifiées de collecte de statistiques.

### 8. `PerformanceMonitorService` : Surveillance en Temps Réel et Rapports

Ce service permet la surveillance en temps réel et la création de rapports.

**Fonctionnalités Clés :**

*   **`getCurrentMetrics()` :** Récupère des métriques en temps réel telles que l'utilisation du CPU, l'utilisation de la mémoire et l'utilisation des E/S.
*   **`getAWRReport()` :** Fournit un rapport Automatic Workload Repository contenant les principales instructions SQL.
*   **`getAWRChartData(String sqlIdFilter)` :** Renvoie le rapport AWR dans un format approprié pour la génération de graphiques.
*   **`getASHReport()` :** Fournit des données Active Session History.
*   **`getASHChartData(String eventFilter)` :** Renvoie les données ASH dans un format adapté aux graphiques.

## Pile Technologique

*   Java 17
*   Spring Boot
*   Spring Data JPA
*   Base de données Oracle
*   JDBC Template
*   Docker (pour la conteneurisation)

## Mise en Route

1.  **Java 17 :** Assurez-vous que Java 17 est installé.
2.  **Base de données Oracle :** Accès à une instance de base de données Oracle.
3.  **Docker :** Requis pour exécuter le conteneur de la base de données Oracle.
4.  **Projet Spring Boot :** Configurez un projet Spring Boot.

## Configuration

Configurez les propriétés d'application suivantes :

*   Informations de connexion à la base de données (URL, nom d'utilisateur, mot de passe).
*   Chemins vers les scripts de sauvegarde et de restauration RMAN.
*   Expressions cron pour la planification des sauvegardes.
*   Délai d'attente pour les processus de restauration.

## Utilisation

Importez les services dans votre application Spring Boot et utilisez les méthodes fournies.
