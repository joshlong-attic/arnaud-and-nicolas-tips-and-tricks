create table if not exists orders (
    id serial primary key,
    product_id  int not null ,
    quantity int not null default 0
) ;