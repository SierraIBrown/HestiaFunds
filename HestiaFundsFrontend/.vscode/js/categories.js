document.addEventListener("DOMContentLoaded", () => {
    const categoriesContainer = document.getElementById("categories-container");
    const categoryForm = document.getElementById("category-form");

    const API_BASE_URL = "http://localhost:8080/api";

    //Fetch and display categories
    async function fetchCategories(){
        try{
            const response = await fetch(`${API_BASE_URL}/categories`);
            const categories = await response.json();

            //Clear the container
            categoriesContainer.innerHTML = "";

            //Populate the list
            categories.forEach((category) => {
                const categoryLabel = document.createElement("span");
                categoryLabel.className = "category-label";
                categoryLabel.textContent = category.name;
                categoryLabel.style.backgroundColor = category.color || "#ddd";

                //Add edit and delete buttons for user-created categories
                if(!category.preloaded){
                    const editButton = document.createElement("button");
                    editButton.textContent = "Edit";
                    editButton.className = "edit-btn";
                    editButton.onclick = () => editCategory(category.id);

                    const deleteButton = document.createElement("button");
                    deleteButton.textContent = "Delete";
                    deleteButton.className = "delete-btn";
                    deleteButton.onclick = () => deleteCategory(category.id);

                    categoryLabel.appendChild(editButton);
                    categoryLabel.appendChild(deleteButton);
                }
                categoriesContainer.appendChild(categoryLabel);
            });
            addPlusButton();
            showNotification("Categories loaded.", "info");
        }
        catch(error){
            console.error("Error fetching categories:", error);
            showNotification("Error fetching categories", "error");
        }
    }

    //Show the modal for adding a new category
    function showAddCategoryModal(){
        showModal(
            "Add New Category",
            `
            <form id="modal-category-form">
                <label for="modal-category-name">Category Name:</label>
                <input type="text" id="modal-category-name" placeholder="Enter category name" required>
                <label for="modal-category-color">Tag Color:</label>
                <input type="color" id="modal-category-color" value="#cccccc">
            </form>
            `,
            async () => {
                const name = document.getElementById("modal-category-name").value;
                const color = document.getElementById("modal-category-color").value;

                if(!name.trim()){
                    showNotification("Category name cannot be empty!", "warning");
                    return;
                }

                try{
                    const response = await fetch(`${API_BASE_URL}/categories`, {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                        },
                        body: JSON.stringify({ name, color }),
                    });
        
                    if(response.ok){
                        showNotification("Category added successfully!", "success");
                        fetchCategories();
                    }
                    else{
                        const error = await response.text();
                        showNotification(`Failed to add category: ${error}`, "error");
                    }
                }
                catch(error){
                    console.error("Error adding category:", error);
                    showNotification("An unexpected error occurred. Please try again.", "error");
                }
            },
            "add"
        );
    }

    //Edit a category
    async function editCategory(id){
        showModal(
            "Edit Category",
            `
            <label for="new-category-name">New Category Name:</label>
            <input type="text" id="new-category-name" placeholder="Enter new name"> 
            `,
            async () => {
                const newName = document.getElementById("new-category-name").value;

                if(!newName || newName.trim() === ""){
                    showNotification("Category name cannot be empty!", "warning");
                    return;
                }

                try{
                    const response = await fetch(`${API_BASE_URL}/categories/${id}`, {
                        method: "PUT",
                        headers: {
                            "Content-Type": "application/json",
                        },
                        body: JSON.stringify({ name: newName }),
                    });

                    if(response.ok){
                        showNotification("Category updates successfully!", "success");
                        categoryForm.reset();
                        fetchCategories();
                    }
                    else{
                        showNotification("Failed to update category.", "error");
                    }
                }
                catch(error){
                    console.error("Error updating category:", error);
                    showNotification("Error updating category.", "error");
                }
            },
            "edit"
        );
    }

    //Delete a cateogry
    async function deleteCategory(id){
        showModal(
            "Delete Category",
            "<p>Are you sure you want to delete this category? This action cannot be undone.</p>",
            async () => {
                try{
                    const response = await fetch(`${API_BASE_URL}/categories/${id}`, {
                        method: "DELETE",
                    });
        
                    if(response.ok){
                        showNotification("Category deleted successfully!", "success");
                        fetchCategories();
                    }
                    else{
                        showNotification("Failed to delete category.", "error");
                    }
                }
                catch(error){
                    console.error("Error deleting category:", error);
                    showNotification("Error deleting category.", "error");
                }
            },
            "delete"
        );
    }

    // Add a "+" button for opening the modal form
    function addPlusButton(){
        const plusButton = document.createElement("button");
        plusButton.textContent = "+";
        plusButton.className = "add-category-btn";
        plusButton.onclick = () => showAddCategoryModal();
        categoriesContainer.appendChild(plusButton);
    }

    //Handle back button
    document.getElementById("back-to-transactions-btn").addEventListener("click", () => {
        window.location.href = "../html/transactions.html";
    });

    //Show a modal
    function showModal(title, content, onConfirm, type = "info"){
        //Create the modal overlay
        const modalOverlay = document.createElement("div");
        modalOverlay.className = "modal-overlay";

        //Create the modal container
        const modal = document.createElement("div");
        modal.className = "modal ${type}";

        //Create the modal title
        const modalTitle = document.createElement("h3");
        modalTitle.textContent = title;
        modal.appendChild(modalTitle);

        //Add the modal content
        const modalContent = document.createElement("div");
        modalContent.className = "modal-content";
        modalContent.innerHTML = content;
        modal.appendChild(modalContent);

        //Add action buttons
        const actions = document.createElement("div");
        actions.className = "modal-actions";

        const confirmButton = document.createElement("button");
        confirmButton.textContent = "Confirm";
        confirmButton.className = "modal-confirm-btn";
        confirmButton.onclick = () => {
            onConfirm();
            modalOverlay.remove();
        };

        const cancelButton = document.createElement("button");
        cancelButton.textContent = "Cancel";
        cancelButton.className = "modal-cancel-btn";
        cancelButton.onclick = () => modalOverlay.remove();

        actions.appendChild(cancelButton);
        actions.appendChild(confirmButton);
        modal.appendChild(actions);

        modalOverlay.appendChild(modal);
        document.body.appendChild(modalOverlay);
    }

    //Notification
    function showNotification(message, type = "success"){
        const notificationContainer = document.getElementById("notification-container");

        //Create the notification element
        const notification = document.createElement("div");
        notification.className = `notification ${type}`;
        notification.textContent = message;

        //Add a close button
        const closeButton = document.createElement("button");
        closeButton.innerHTML = "&times;"
        closeButton.onclick = () => notification.remove();
        notification.appendChild(closeButton);

        //Append it to the container
        notificationContainer.appendChild(notification);

        //Remove it after 4 seconds
        setTimeout(() => {
            notification.remove();
        }, 4000);
    }

    //Initial fetch
    fetchCategories();
});