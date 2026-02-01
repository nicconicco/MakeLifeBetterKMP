# Excel Template Guide for MakeLifeBetter App

This document describes the format required for Excel files that can be imported into the MakeLifeBetter app.

## File Format

The Excel file must be in `.xlsx` format and contain the following sheets (tabs):

## Required Sheets

### 1. Eventos (Events)

| Column | Field Name | Type | Required | Description |
|--------|------------|------|----------|-------------|
| A | titulo | String | Yes | Event title |
| B | subtitulo | String | No | Event subtitle |
| C | descricao | String | No | Event description |
| D | hora | String | No | Event time (e.g., "09:00") |
| E | lugar | String | No | Event location/venue |
| F | categoria | String | No | Event category |

**Example:**
| titulo | subtitulo | descricao | hora | lugar | categoria |
|--------|-----------|-----------|------|-------|-----------|
| Cerimonia de Abertura | Bem-vindos | Cerimonia oficial | 09:00 | Salao Principal | cerimonia |
| Coffee Break | Pausa | Networking | 10:30 | Area de Convivencia | intervalo |

---

### 2. Localizacao (Location)

| Column | Field Name | Type | Required | Description |
|--------|------------|------|----------|-------------|
| A | name | String | Yes | Location name |
| B | address | String | No | Street address |
| C | city | String | No | City and state |
| D | latitude | Number | No | GPS latitude |
| E | longitude | Number | No | GPS longitude |

**Example:**
| name | address | city | latitude | longitude |
|------|---------|------|----------|-----------|
| Centro de Convencoes | Av. Principal, 1000 | Sao Paulo - SP | -23.550520 | -46.633308 |

---

### 3. Contatos (Contacts)

| Column | Field Name | Type | Required | Description |
|--------|------------|------|----------|-------------|
| A | name | String | Yes | Contact name/department |
| B | phone | String | Yes | Phone number |

**Example:**
| name | phone |
|------|-------|
| Recepcao | (11) 1234-5678 |
| Organizacao | (11) 9876-5432 |
| Emergencia | (11) 9999-9999 |

---

## Optional Sheets (Store)

### 4. Categorias (Categories)

| Column | Field Name | Type | Required | Description |
|--------|------------|------|----------|-------------|
| A | nome | String | Yes | Category name |
| B | ordem | Number | No | Display order (defaults to row index) |

**Example:**
| nome | ordem |
|------|-------|
| Camisetas | 1 |
| Canecas | 2 |
| Acessorios | 3 |
| Adesivos | 4 |

---

### 5. Produtos (Products)

| Column | Field Name | Type | Required | Description |
|--------|------------|------|----------|-------------|
| A | nome | String | Yes | Product name |
| B | subtitulo | String | No | Product subtitle (e.g., "100% Algodao") |
| C | descricao | String | No | Product description |
| D | preco | Number | Yes | Product price (use . or , as decimal separator) |
| E | imagem | String | No | URL to product image |
| F | categoria | String | No | Category ID from Firebase |
| G | ativo | Boolean | No | Whether product is active (defaults to "true") |

**Example:**
| nome | subtitulo | descricao | preco | imagem | categoria | ativo |
|------|-----------|-----------|-------|----------|-------------|-------|
| Camiseta Dev Life | 100% Algodao | Camiseta confortavel | 79.90 | https://... | abc123 | true |
| Caneca Coffee & Code | 350ml | Caneca perfeita | 49.90 | https://... | def456 | true |

---

## Notes

1. **Header Row**: The first row of each sheet should contain the column headers as shown above.

2. **Empty Rows**: Empty rows are ignored during import.

3. **Data Validation**:
   - Required fields must not be empty
   - Numeric fields (preco, latitude, longitude, ordem) should contain valid numbers
   - Boolean fields accept "true" or "false" (case insensitive)

4. **Categories First**: When importing, categories are processed before products to ensure category references are valid.

5. **Character Encoding**: Use UTF-8 encoding for special characters (accents, etc.).

---

## Firebase Collection Structure

After import, data will be stored in the following Firebase Firestore collections:

- `eventos` - Events
- `event_location` - Location data
  - `contacts` (subcollection) - Contact information
- `categorias` - Product categories
- `produtos` - Products
- `carrinho` - Shopping carts (per user)
  - `items` (subcollection) - Cart items
- `pedidos` - Orders
- `users` - User accounts
  - `isAdmin` field determines admin privileges

---

## Admin Users

To make a user an admin in Firebase:

1. Go to Firebase Console > Firestore Database
2. Find the user document in the `users` collection
3. Add or edit the field: `isAdmin: true`

Admin users can:
- Create, edit, and delete products
- Manage orders
- Access admin panel features
