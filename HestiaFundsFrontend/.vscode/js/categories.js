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
            showNotification("Categories loaded.", "info");
        }
        catch(error){
            console.error("Error fetching categories:", error);
            showNotification("Error fetching categories", "error");
        }
    }

    //Add a new category
    async function addCategory(event){
        event.preventDefault();

        const name = document.getElementById("name").value;

        if(name.trim() === ""){
            showNotification("Category name cannot be empty!", warning);
            return;
        }

        try{
            const response = await fetch(`${API_BASE_URL}/categories`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ name }),
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
    }

    //Edit a category
    async function editCategory(id){
        const newName = prompt("Enter new category name:");

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
    }

    //Delete a cateogry
    async function deleteCategory(id){
        if(!confirm("Are you sure you want to delete this category?")){
            return;
        }

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

    //Attach event listener to the form
    categoryForm.addEventListener("submit", addCategory);

    //Initial fetch
    fetchCategories();
});