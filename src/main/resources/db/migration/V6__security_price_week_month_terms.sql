alter table security_price
  alter column term drop not null;

alter table security_price
  rename column term to term_months;

alter table security_price
  add column term_weeks integer;

alter table security_price
  add constraint constraint_term_one_nonnull check (num_nonnulls(term_weeks, term_months) = 1);

alter table security_price
  drop column yield_to_maturity;

alter table security_price
  drop column discount_price;

alter table security_price
  drop column par_value;

alter table security_price
  add constraint unique_price_content unique nulls not distinct
    (security_id, issued_timestamp, term_weeks, term_months);

comment on column security_price.term_weeks is 'unit: weeks';
