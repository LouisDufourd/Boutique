<#macro formulaire>
    <div class="modal fade" id="createShopForm" tabindex="-1" aria-labelledby="createShopLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="createShopLabel">Créer un magasin</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="username" class="form-label">Nom du magasin</label>
                        <input type="text" class="form-control" name="shopName" id="shopName" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" onclick="submit()" class="btn btn-primary">Créer un magasin</button>
                </div>
            </div>
        </div>
    </div>
    <script>
        function submit() {
            let shopName = document.getElementById("shopName")
            document.location.href = "/createShop?url=" + document.location.pathname + "&shopName=" + shopName.value
        }
    </script>
</#macro>