<#import "navbar.ftl" as nav>
<#import "header.ftl" as head>
<#import "form.ftl" as form>
<!DOCTYPE html>
<html lang="fr">
<head>
    <@head.header>
    </@head.header>
    <title>Profile</title>
</head>
<body>
<@nav.navbar>
</@nav.navbar>
<!--Gestion des erreur des formulaires-->
<#switch data.editerror>
    <#case 1>
        <div class="alert alert-danger" role="alert">
            Le nom d'utilisateur est déjà utiliser
        </div>
        <#break>
    <#case 2>
        <div class="alert alert-danger" role="alert">
            Le nom d'utilisateur n'est pas valide
        </div>
        <#break>
    <#case 3>
        <div class="alert alert-danger" role="alert">
            Le mot de passe n'est pas valide
        </div>
        <#break>
</#switch>
<#switch data.connectedpage.createshoperror>
    <#case 1>
        <div class="alert alert-danger" role="alert">
            Le nom de la boutique est déjà utilisé
        </div>
        <#break>
    <#case 2>
        <div class="alert alert-danger" role="alert">
            Le nom de la boutique est invalide
        </div>
        <#break>
</#switch>
<div class="container">
    <div class="main-body">
        <div class="row gutters-sm">
            <!--Information-->
            <div class="col-md-4 mb-3">
                <div class="card">
                    <div class="card-body">
                        <div class="d-flex flex-column align-items-center text-center">
                            <div class="mt-3">
                                <h2>Information</h2>
                                <p class="text-secondary mb-1">Date de création du compte : ${data.user.createTime}</p>
                                <p class="text-muted mb-1">Solde : ${data.user.solde}€</p>
                                <p class="text-muted font-size-sm">Nombre de slot : ${data.user.nombreSlot}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!--Modification du profile-->
            <div class="col-md-8">
                <form method="get" action="/editUser">
                    <div class="card mb-3">
                        <div class="card-body">
                            <div class="row">
                                <div class="col-sm-3">
                                    <label for="username">
                                        <h6 class="mb-0">Nom d'utilisateur</h6>
                                    </label>
                                </div>
                                <div class="col-sm-9 text-secondary">
                                    <input type="text" id="username" name="username" class="form-control"
                                           value="${data.user.username}" required>
                                </div>
                            </div>
                            <hr>
                            <div class="row">
                                <div class="col-sm-3">
                                    <label for="password">
                                        <h6 class="mb-0">Mots de passe</h6>
                                    </label>
                                </div>
                                <div class="col-sm-9 text-secondary">
                                    <input type="password" id="password" class="form-control" value="" name="password"
                                           required/>
                                </div>
                            </div>
                            <hr>
                            <div class="row">
                                <div class="col-sm-3">
                                    <label for="email">
                                        <h6 class="mb-0">Email</h6>
                                    </label>
                                </div>
                                <div class="col-sm-9 text-secondary">
                                    <input type="email" id="email" class="form-control" name="email"
                                           value="${data.user.email}" required/>
                                </div>
                            </div>
                            <hr>
                            <div class="row">
                                <div class="col-sm-12">
                                    <button type="submit" class="btn btn-primary">Sauvegarder</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <!--Inventaire-->
            <div class="col-md-9">
                <h1>Inventaire</h1>
                <div class="card">
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                            <div class="col-md-1 card">

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<@form.formulaire>
</@form.formulaire>
</body>
<script src="/assets/js/bootstrap.min.js"></script>
</html>