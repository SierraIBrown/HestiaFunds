document.addEventListener("DOMContentLoaded", () => {
    const transactionsBody = document.getElementById("transactions-body");
    const categorySelect = document.getElementById("category");
    const transactionForm = document.getElementById("transaction-form");

    //Fetch transactions and populate table
    async function fetchTransactions(){
        try{
            const response = await fetch("/api/transactions");
            const transactions = await response.json();

            //Clear table
            transactionsBody.innerHTML = "";

            //Populate
            transactions.forEach((transaction) => {
                const row = document.getElementById("tr");

                row.innerHTML = `
                <td>${transaction.date}</td>
                <td>${transaction.amount.toFixed(2)}</td>
                <td>${transaction.category.name}</td>
                <td>${transaction.description}</td>
                <td>
                    <button data-id="${transaction.id}" class="edit-btn">Edit</button>
                    <button data-id="${transaction.id}" class="delete-btn">Delete</button>
                `;

                transactionsBody.appendChild(row);
            });
        }
        catch(error){
            console.error("Error fetching transactions:", error);
        }
    }

    //Fetch categories
    async function fetchCategories(){
        try{
            const response = await fetch("/api/categories");
            const categories = await response.json();

            categories.forEach((category) => {
                const option = document.createElement("option");
                option.value = category.id;
                option.textContent = category.name;
                categorySelect.appendChild(option);
            });
        }
        catch(error){
            console.error("Error fetching categories:", error);
        }
    }

    //Handle form submission
    transactionForm.addEventListener("submit", async(event) => {
        event.preventDefault();

        const formData = new FormData(transactionForm);
        const newTransaction = {
            date: formData.get("date"),
            description: formData.get("description"),
            category: { id: formData.get("category") },
            amount: parseFloat(formData.get("amount")),
        };

        try{
            const response = await fetch("/api/transactions", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(newTransaction),
            });

            if(response.ok){
                fetchTransactions();
                transactionForm.reset();
            }
            else{
                console.error("Error adding transaction:", await response.text());
            }
        }
        catch(error){
            console.error("Error adding transaction:", error);
        }
    });

    //Initial fetches
    fetchTransactions();
    fetchCategories();
});