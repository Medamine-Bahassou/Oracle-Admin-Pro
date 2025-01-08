# Descriptions des Contrôleurs

Ce document fournit des descriptions des contrôleurs au sein de l'application de gestion de base de données Oracle. Chaque contrôleur est responsable de la gestion des points de terminaison API spécifiques liés à divers services.

## 1. `UserController`

Ce contrôleur gère les points de terminaison API liés aux utilisateurs.

**Points de terminaison :**

*   **`POST /api/users`** : Crée un nouvel utilisateur.
    *   Corps de la requête : Objet `User` contenant les détails de l'utilisateur.
    *   Réponse : Objet `User` créé.
*   **`GET /api/users`** : Récupère tous les utilisateurs.
    *   Réponse : Liste d'objets `User`.
*   **`GET /api/users/{id}`** : Récupère un utilisateur par ID.
    *   Variable de chemin : `id` - ID de l'utilisateur à récupérer.
    *   Réponse : Objet `User`.
*   **`PUT /api/users/{id}`** : Met à jour un utilisateur par ID.
    *   Variable de chemin : `id` - ID de l'utilisateur à mettre à jour.
    *   Corps de la requête : Objet `User` contenant les détails de l'utilisateur mis à jour.
    *   Réponse : Objet `User` mis à jour.
*   **`DELETE /api/users/{id}`** : Supprime un utilisateur par ID.
    *   Variable de chemin : `id` - ID de l'utilisateur à supprimer.
    *   Réponse : Statut OK.

## 2. `TableManagementController`

Ce contrôleur gère les points de terminaison API liés à la gestion des tables.

**Points de terminaison :**

*   **`POST /api/tables/create`** : Crée une nouvelle table.
    *   Corps de la requête : Objet `TableRequest` contenant le nom de la table et les détails des colonnes.
    *   Réponse : Message de succès ou d'échec.
*   **`GET /api/tables/list`** : Récupère la liste de toutes les tables.
    *   Réponse : Liste d'objets `TableInfo`.
*   **`GET /api/tables/{tableName}`** : Récupère les informations d'une table.
    *   Variable de chemin : `tableName` - Nom de la table à récupérer.
    *   Réponse : Objet `TableInfo` ou 404 Not Found si la table n'existe pas.
*   **`PUT /api/tables/{tableName}`** : Met à jour une table existante.
    *   Variable de chemin : `tableName` - Nom de la table à mettre à jour.
    *   Corps de la requête : Liste des noms de colonnes.
    *   Réponse : Message de succès ou d'échec.
*   **`DELETE /api/tables/{tableName}`** : Supprime une table.
    *   Variable de chemin : `tableName` - Nom de la table à supprimer.
    *   Réponse : Message de succès ou d'échec.
 *  **`GET /api/tables/columns/{tableName}`**: Obtient la liste des colonnes d'une table.
    *   Variable de chemin: `tableName` - Nom de la table à partir de laquelle obtenir les colonnes.
    *   Réponse: Liste des noms de colonnes.

## 3. `SecurityController`

Ce contrôleur gère les points de terminaison API liés à la sécurité.

**Points de terminaison :**

*   **`POST /api/security/tde/enable`** : Active le Transparent Data Encryption (TDE) pour une colonne de table spécifiée.
    *   Paramètres de la requête : `tableName`, `columnName`.
    *   Réponse : Message de succès ou d'échec.
*   **`POST /api/security/audit/enable`** : Active l'audit sur une table spécifiée.
    *   Paramètre de la requête : `tableName`.
    *   Réponse : Message de succès ou d'échec.
*   **`POST /api/security/vpd/configure`** : Configure Virtual Private Database (VPD) pour une table.
    *   Paramètres de la requête : `tableName`, `policyFunction`.
    *   Réponse : Message de succès ou d'échec.
*   **`GET /api/security/tables`**: Obtient la liste de toutes les tables.
      * Réponse: Liste des noms de tables ou message d'erreur.
*   **`GET /api/security/columns`**: Obtient la liste des colonnes d'une table spécifiée.
      *   Paramètres de la requête: `tableName`.
      *  Réponse: Liste des noms de colonnes ou message d'erreur.

## 4. `RoleController`

Ce contrôleur gère les points de terminaison API liés à la gestion des rôles.

**Points de terminaison :**

*   **`POST /api/roles/assign`** : Attribue un rôle à un utilisateur.
    *   Corps de la requête : Objet `RoleRequest` contenant le nom d'utilisateur et le nom du rôle.
    *   Réponse : Message de succès ou d'échec.
*   **`POST /api/roles/create`** : Crée un nouveau rôle.
    *   Corps de la requête : Objet `RoleRequest` contenant le nom du rôle.
    *   Réponse : Message de succès ou d'échec.
*   **`POST /api/roles/grantPrivileges`** : Accorde des privilèges à un rôle.
    *  Corps de la requête : Objet `RoleRequest` contenant le nom du rôle et les privilèges.
    *   Réponse : Message de succès ou d'échec.
*   **`POST /api/roles/revoke`** : Révoque un rôle d'un utilisateur.
    *   Paramètres de la requête : `username`, `role`.
    *   Réponse : Message de succès ou d'échec.
*   **`DELETE /api/roles/drop`** : Supprime un rôle.
    *   Paramètre de la requête : `roleName`.
    *   Réponse : Message de succès ou d'échec.
*   **`GET /api/roles`** : Récupère la liste de tous les rôles.
    *   Réponse: Liste des rôles.
*  **`GET /api/roles/{username}`**: Obtient les privilèges d'un utilisateur spécifié.
     *  Variable de chemin: `username`.
     * Réponse: Liste des privilèges de l'utilisateur.
*    **`GET /api/roles/{roleName}/privileges`**: Obtient les privilèges d'un rôle spécifié.
     * Variable de chemin: `roleName`.
     * Réponse: Liste des privilèges du rôle.
*   **`GET /api/roles/{username}/roles`**: Obtient les rôles d'un utilisateur spécifié.
     * Variable de chemin: `username`.
     * Réponse: Liste des rôles de l'utilisateur.

## 5. `BackupController`

Ce contrôleur gère les points de terminaison API liés aux sauvegardes.

**Points de terminaison :**

*   **`POST /api/backups/full`** : Effectue une sauvegarde complète de la base de données.
    *   Réponse : Message de résultat.
*   **`POST /api/backups/incremental`** : Effectue une sauvegarde incrémentielle de la base de données.
    *   Paramètre de la requête : `level`.
    *   Réponse : Message de résultat.
*   **`GET /api/backups/history`** : Récupère tout l'historique des sauvegardes.
     *   Réponse: Liste des objets `BackupHistory`
*    **`POST /api/backups/restore`**: Effectue une restauration.
     *    Réponse: Message de résultat.
*   **`GET /api/backups/history/date`**: Récupère les sauvegardes en fonction d'une plage de date.
      * Paramètres de la requête: `startDate`, `endDate`.
      *  Réponse: Liste des objets `BackupHistory`.

## 6. `PerformanceController`

Ce contrôleur expose des points de terminaison API pour la surveillance des performances.

**Points de terminaison :**

*   **`GET /api/performance/metrics`** : Récupère les métriques de performance.
    *   Réponse : Objet `PerformanceMetricsDTO`.
*   **`GET /api/performance/awr`** : Récupère le rapport Automatic Workload Repository (AWR).
    *   Réponse : Liste d'objets `AWRReportDTO`.
*   **`GET /api/performance/awr-chart`** : Récupère les données du rapport AWR pour la génération de graphiques.
    * Paramètre de requête: sqlIdFilter (Optionnel).
    *   Réponse: Map des données de graphique AWR.
*   **`GET /api/performance/ash`** : Récupère le rapport Active Session History (ASH).
    *   Réponse : Liste d'objets `ASHReportDTO`.
*   **`GET /api/performance/ash-chart`** : Récupère les données du rapport ASH pour la génération de graphiques.
   *   Paramètre de requête: eventFilter (Optionnel).
    *   Réponse: Map des données de graphique ASH.
