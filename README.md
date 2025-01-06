<div align="center">
  <img src="https://github.com/user-attachments/assets/9b92324b-06ea-4e30-a2cf-4a1644d3e6b4" />
</div>

# Oracle Admin Pro

![image](https://github.com/user-attachments/assets/7a5b5154-51c8-40ec-931e-d86a6677aa06)

## Présentation du Projet

Oracle Admin Pro est une application d'administration Web conçue pour simplifier les tâches de gestion de base de données pour les bases de données Oracle. Développée en utilisant Java EE (Spring Boot) pour le backend et Angular pour le frontend, cette application vise à fournir une interface conviviale pour les opérations administratives clés.

## Fonctionnalités Principales

D'après le document fourni, le projet vise à offrir les fonctionnalités suivantes :

*   **Gestion des Utilisateurs :**
    *   Créer, modifier et supprimer des utilisateurs Oracle.
    *   Gérer les rôles d'utilisateur et les privilèges d'accès via une interface web.
    *   Configurer les quotas d'espace et les politiques de mot de passe.
*   **Sauvegarde et Restauration :**
    *   Lancer des sauvegardes complètes et incrémentielles en utilisant RMAN directement depuis l'application.
    *   Afficher l'historique des sauvegardes et les options de restauration pour des dates spécifiques.
    *   Planifier des sauvegardes automatisées avec des plans personnalisables.
*   **Sécurité des Données :**
    *   Configurer les politiques de Transparent Data Encryption (TDE).
    *   Gérer les Virtual Private Databases (VPD) pour contrôler l'accès aux données.
    *   Activer les audits de sécurité.
*   **Surveillance des Performances :**
    *   Afficher les rapports AWR et ASH avec visualisation graphique (par exemple, en utilisant Chart.js).
    *   Tableau de bord en temps réel pour l'utilisation des ressources (CPU, E/S, mémoire).
*   **Optimisation des Performances :**
    *   Interface pour identifier et optimiser les requêtes lentes avec SQL Tuning Advisor.
    *   Planifier le recalcul des statistiques des tables et des index.
*   **Haute Disponibilité :**
    *   Configurer et surveiller Oracle Data Guard.
    *   Simuler des basculements et des restaurations avec des rapports sur la disponibilité.

## Équipe

*   **Mohamed Amine BAHASSOU**
*   **Hodaifa ECHFFANI**

## Superviseur

*   **Mohamed BEN AHMED**

## Technologies Utilisées

*   **Base de données :** Oracle 19c
*   **Backend :**
    *   Java EE (Spring Boot)
    *   Hibernate (ORM)
    *   JDBC (pour l'exécution des commandes SQL)
    *   Spring Security (authentification et gestion de session)
    *   REST ou SOAP (implémentation de services web)
*   **Frontend :**
    *   Angular
    *   Tailwind CSS (pour le stylage)
    *   Daisy UI (pour les composants d'interface utilisateur)
    *   Chart.js (pour les visualisations)

## Structure du Projet

Le projet est divisé en deux parties principales :

1.  **Backend :** Une application Spring Boot responsable de la gestion des interactions avec la base de données, de la sécurité et de la logique métier.
2.  **Frontend :** Une application Angular qui fournit l'interface utilisateur pour le panneau d'administration.

## Installation et Configuration

### Prérequis

*   Java Development Kit (JDK) 17 ou supérieur
*   Maven
*   Node.js et npm (pour le développement Angular)
*   Base de données Oracle 19c installée et accessible

### Configuration du Backend (Spring Boot)

1.  **Cloner le dépôt :**
    ```bash
    git clone <repository_url>
    cd oracle-admin-pro/backend
    ```
2.  **Installer les dépendances :**
    *   Le `pom.xml` affiche toutes les dépendances nécessaires ; Maven les téléchargera automatiquement.
3.  **Configurer la base de données :**
    *   Mettre à jour `application.properties` ou `application.yml` avec les informations de connexion de votre base de données Oracle.
4.  **Compiler et exécuter :**
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
    Le serveur backend s'exécutera sur le port défini dans `application.properties` ou `application.yml`.

### Configuration du Frontend (Angular)

1.  **Naviguer vers le répertoire du Frontend :**
    ```bash
    cd ../frontend
    ```
2.  **Installer les dépendances Node :**
    ```bash
    npm install
    ```
3.  **Exécuter l'application Angular :**
    ```bash
    ng serve
    ```
    Le serveur frontend s'exécutera sur le port `4200`.

## Utilisation

1.  Accédez à l'application dans votre navigateur web en utilisant l'URL et le port du serveur frontend (par exemple, `http://localhost:4200`).
2.  Connectez-vous avec vos identifiants (actuellement `admin/admin` dans l'exemple).
3.  Naviguez dans le panneau d'administration pour explorer les fonctionnalités.

## Contribution

Si vous souhaitez contribuer au projet, veuillez suivre ces directives :

1.  Forkez le dépôt.
2.  Créez une nouvelle branche pour votre fonctionnalité ou correction de bug.
3.  Validez vos modifications avec des messages clairs et descriptifs.
4.  Soumettez une demande de tirage (pull request) au dépôt principal.
