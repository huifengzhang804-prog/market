const arr = [1, 2, 3]
const newAe = [1, 2]
console.log(
    arr.filter((item) => {
        return !newAe.some((v) => {
            return v === item
        })
    }),
)
