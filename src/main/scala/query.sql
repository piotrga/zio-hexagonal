


select
    patients.age_group,
    patients.city,
    sum(patients.hospitalised_days),
    sum(icecream_purchases.gramms),
    sum(exercise_sessions.minutes)
from
    national_health_service.patients
        join gyms_portugal.exercise_sessions using (personid)
        join joes_ice_creams.icecream_purchases using (personid)
group by age_group, city




